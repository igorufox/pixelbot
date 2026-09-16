# Migration notes

*[Русская версия](migration.ru.md)*

What changed between the 2014 archives and this repository, and why.

## Where the source came from

Two archives, and they are not the same vintage.

`src.zip` is a source dump from June 2014. `workspace.zip` is the Eclipse workspace from July 2014,
and it is newer: its `ScriptEditor` is larger, its `Releaser` has had its wildcard imports expanded,
and — the deciding difference — the `script/*/js/jre` packages have been deleted from it.

Those packages targeted `sun.org.mozilla.javascript.internal`, the Rhino that used to be bundled
inside the JDK. Java 8 replaced it with Nashorn and the package disappeared, and the workspace's
`.classpath` is set to `JavaSE-1.8`. The author had already hit that and removed the backends. So
the July workspace is what this repository is built from; the June dump would have meant
resurrecting four packages against an API that no longer exists.

## Renaming

Everything that carried the old name was renamed: the two root packages, the main window and
launcher classes, the logger, the namespace the script library exposes to scripts, the window
title, the resource paths, the preferences node and the jar. The captcha puzzle kinds had been
named after the minigames they were cut from; they are now `edge`, `tile`, `texture` and `relic`,
and the solver classes follow.

One of the two root packages held a legacy serialised element format. Nothing references it from
code — it is read reflectively when importing old files — so it now sits under `pixelbot.legacy`.

Renaming packages breaks Java serialisation compatibility with any `.elg` or `.ser` files produced
by an older build, since class names are written into the stream. None ship here, so that is moot,
but it is worth knowing if you have some of your own.

## Making it build

JDK internals the 2014 build relied on, and what replaced them:

| Was | Now | Gone since |
| --- | --- | --- |
| `com.sun.awt.AWTUtilities` | `Window.setBackground` / `setShape`, `GraphicsDevice.isWindowTranslucencySupported` | Java 9 |
| `sun.net.www.MimeTable` | a local content-type map in `ScriptEditor` | Java 9 |
| `java.awt.peer.RobotPeer`, reached by reflection | `Robot.createScreenCapture` | Java 9 |
| `javax.xml.bind.DatatypeConverter` | `java.util.Base64`, `java.util.HexFormat` | Java 11 |

Library-level changes:

* **Eclipse WST JSDT** supplied the JavaScript lexer and formatter for the editor. It was never
  published to Maven Central and the WTP project is retired. JDT's equivalents are used instead: the
  two APIs are identical in shape, because JSDT was forked from JDT, and Java's token grammar covers
  JavaScript closely enough for colouring. Keywords JDT does not know — `function`, `var`, `let` —
  lex as identifiers, which is the visible cost.
* **Apache POI** replaced the `Cell.CELL_TYPE_*` int constants with the `CellType` enum, and dropped
  `setCellType(STRING)` as a way to stringify numbers; `DataFormatter` does that now.
* **Swing generics**: `JComboBox<E>`, `ListCellRenderer<E>`, `DefaultComboBoxModel<E>`, and
  `TreeNode.children()` whose return type was tightened in Java 9.
* **Eclipse JDT**'s `IErrorHandlingPolicy` gained `ignoreAllErrors()`.
* The vendored copy of `org.apache.commons.net.ntp` in the source tree was replaced by the real
  `commons-net` dependency.

And one that only showed up at runtime:

* `PixelBotApp` built its class loader with a `null` parent. Before the module system that meant all
  of `rt.jar`; today the bootstrap loader defines only `java.base`, `java.desktop` and a few others,
  while `java.compiler` lives in the platform loader. Swing came up fine and then the embedded Java
  backend died with `NoClassDefFoundError: javax/tools/ToolProvider`. The parent is now
  `ClassLoader.getPlatformClassLoader()`.

## Bugs found on the way

None of these were introduced by the migration; they were all there.

* **`BaseTreeNode.children()` called itself.** `return children();` — any caller got a
  `StackOverflowError`. Now returns an enumeration over the node's children, with the signature Java
  9 requires.
* **The search discarded ties.** `Algorithm.Result` ordered by weight alone and results were
  collected in a `TreeSet`, so every arrangement that scored the same as one already seen was
  silently dropped. `compareTo` now breaks ties on the permutation, and the search collects into a
  list.
* **Degenerate statistics scored best.** Variance and covariance came straight from commons-math,
  which returns `NaN` for a sample of fewer than two points. `(long) NaN` is `0` — the lowest, and
  therefore best, possible weight — so a blank region beat every real one. Covariance on such a
  sample throws outright, which killed the whole solve. `MomentStats` returns 0 for those cases and
  `MomentStatsTest` pins the behaviour down.
* **Division by an unchecked count.** `calculateMassCenter` divided by the number of ink pixels
  without testing it for zero.
* **Unbounded scans.** `CaptchaSolverFactory.parseDescriptor` walked in from the image edges looking
  for the grid's divider colour with no bound, relying on an `ArrayIndexOutOfBoundsException` to
  stop, which was then reported as a generic failure.
* **An operator-precedence bug.** `ElementDescriptorBase.getImage()` tested
  `image == null && width != 0 || height != 0`, which parses as `(a && b) || c` — so the image was
  re-rasterised on every call. Fixed, with the cache invalidated by `addPixel` and `setDimensions`.
* **`null` from `File.listFiles()`.** `SettingsPanel.loadPrefs` iterated it directly, so a fresh
  install with no `scripts/` directory threw before the window opened.
* **A shutdown that could not complete.** `windowClosing` disposed the window only if `savePrefs()`
  returned true, and did not catch what it threw — so any failure while saving preferences left a
  window that could not be closed.
* **`FourDimensionVarianceAlgorithm`** was an unfinished experiment. It built a 4×4 covariance from
  two sample sets of different lengths and indexed both with the length of the first, so it threw
  whenever those lengths differed; it had transposed indices (`D[0][2] = D[3][0]`) and doubled signs
  (`- -D[0][1]`) in the determinant; and conceptually it treated two unrelated sample sets as four
  coordinates of the same points. Nothing referenced it. It was removed rather than guessed at.

## Optimisations

* **Clustering.** Tile denoising ran commons-math's DBSCAN with `minPts = 0`, which is just
  connected components, at O(n²) over up to fifteen thousand points per tile. `GridClusters` does it
  with a union-find pass over a fixed stencil: same result, roughly four hundred times less work.
  This was the single largest cost in a solve — far larger than the 720 arrangements the search
  exists to evaluate. See [the captcha notes](captcha.md#performance).
* **Scoring without the canvas.** `FastDividedVarianceAlgorithm` derives the band scorer's result
  from precomputed additive moments instead of rebuilding an 87 000-bit canvas 720 times.
* **Parallel search**, and the removal of a measurement harness that was walking the ranked results
  inside the production path.
* `Vector` and `Hashtable` gave way to `ArrayList` and `HashMap` in the code that was touched.

## Logging

Debug output went to `System.out` and `System.err` from inside the scoring loops, while the project
had a perfectly good logging subsystem with its own viewer. Those now log through
`java.util.logging` at `FINE` and `FINEST`, which is where they belonged. Empty `catch` blocks on
the paths that were touched were given a log line.

There are still around seventy empty `catch` blocks and as many `printStackTrace` calls elsewhere in
the codebase. They are the original's, and they have not been touched.

## What was removed

* Every `.elg` pixel template (1009 files), `config/travel.cfg`, and the application icon — all of
  them the previous target's artwork or data. The state and info icon sets went too; nothing
  referenced them.
* The toolbar icons. They came from the Eclipse Platform debug UI, which is EPL, and from at least
  one other set whose origin could not be established. Writing a `NOTICE` for them would have meant
  guessing, so they are generated now — `tools/generate_icons.py` is the only source of any icon the
  program ships.
* `banner.txt`, which held the author's forum address and Skype handle.
* The `js/jre` script backends, as described above.
* The Eclipse workspace metadata, `.classpath` and `.project`.

## What was added

* A Maven build targeting Java 21, producing a runnable shaded jar.
* **GraalJS** and **Nashorn** script backends alongside Rhino: parser, instantiator and scope
  bridge for each, registered through the existing service files. Adding them required no change to
  the application — which is the best evidence that the 2014 service layer was designed properly.
* Two colour-based captcha scorers, including Gallagher's Mahalanobis gradient compatibility.
* A test suite: the statistics, the clustering equivalence, the fast-versus-reference scorer
  agreement, jigsaw reassembly end to end, and both new engines through the scope model.
* `.gitignore`, `.gitattributes`, `LICENSE`, `NOTICE`, and these documents.
