# Architecture

*[Русская версия](architecture.ru.md)*

Four things sit on top of each other: a way of recognising what is on the screen, a way of acting on
it, a service layer that runs scripts, and an IDE for writing them.

## Element recognition

An **element** is a template: a set of pixels, each with a colour and an offset from the element's
origin, captured from the screen once and stored. `pixelbot.elements.ElementDescriptor` holds one,
`Elements` is the collection, and `ElemTools` reads and writes the `elements.elz` archive next to
the application.

Elements are named hierarchically, dot-separated, and the naming is a convention rather than
anything the code enforces — `interface.buttons.close`, for instance. Scripts ask for an element by
name and get back where it is on screen, or nothing.

Matching is exact pixel comparison over a screen grab, not fuzzy image search. That is why the
templates have to be captured from the same rendering they will be matched against, and why the
tooling to capture, test and re-capture them (the *Elements* tab) is as prominent as it is. What it
buys is speed and zero false positives.

`ElementDescriptor` also carries a colour type (`CT_MULTICOLOR`, or a reduced form) and a set of
locales, so the same logical element can have variants.

### Screen and input

`pixelbot.script.helper.RobotPeer` wraps `java.awt.Robot`: it grabs the screen, or a rectangle of
it, into a packed ARGB array, and moves and clicks the mouse and presses keys. `IOHelper` is the
façade the scripts see, and it is where the mouse timing lives — the movement speed, the click
duration and the pauses around it are all randomised inside configured ranges, which is what makes
the pointer behave less like a machine.

`HighlightScreen` is a transparent always-on-top window that draws rectangles around elements as
they are found. It is a debugging aid and it is why the application needs per-pixel window
translucency.

## The script service layer

The part the project is actually built around, and the part that has aged best. Running a script is
broken into four independent concerns, each a service interface discovered through
`META-INF/services`:

```
script/parser/        source or archive  ->  something the engine can run
script/instantiator/  that thing         ->  running it, and publishing what it defined
script/scope/         engine objects    <->  an engine-independent model
script/debuger/       stepping, breakpoints
```

Each has a `general` package with the interfaces and one package per implementation. A new language
or a new engine is a jar on the classpath with a registrator in its service file; nothing in the
application changes.

### Parsers

`IScriptParser` turns a file into a list of `IScriptInstantiator`. Parsers declare the file type
they handle, an id, a display name and a priority, and several parsers may claim the same type — in
which case the *Interpreters* settings tab offers the choice, which is where the JavaScript engine
list comes from.

| Parser | Type | Notes |
| --- | --- | --- |
| `js.rhino`, debug | `djs` | Interpreted, works with the stepping debugger |
| `js.rhino`, compile | `djs` | Compiled to bytecode, faster, no debugger |
| `js.graal` | `djs` | Current ECMAScript, the one to pick for new scripts |
| `js.nashorn` | `djs` | The standalone continuation of the engine Java 8 shipped |
| `java` | `djava` | Compiled in process by the Eclipse batch compiler |
| `jar`, `zip`, `clazz` | | Load already-compiled code |

### Instantiators

`ScriptInstantiatorManager` maps the *class* of a parsed artefact to the factory that knows how to
run it — `org.mozilla.javascript.Script` to the Rhino factory, `org.graalvm.polyglot.Source` to the
Graal one, and so on. Matching is by `isAssignableFrom`, so a factory registered for an interface
picks up whatever concrete type the engine produced.

Instantiating a script means evaluating it and publishing the globals it defined into the shared
`GlobalScope`. Rhino does this directly, because it is handed a live `Scriptable` that writes
through to the scope. Graal and Nashorn keep their own global object, so their factories copy the
globals across afterwards — handles, not data, so later mutation is still visible on both sides.

### The scope model

Between the engines and everything else sits a small model that does not know what an engine is:

```
IScopeObject
  IScopeMap        an object with named members
  IScopeList       an array
  IScopeFunction   something callable, with parameter names
  IScopeString / IScopeNumber / IScopeBoolean / IScopeNative
```

Each backend supplies an `IConverter` that goes both ways: engine object to scope object, and scope
object back to something the engine will accept. `JConverter` handles the parts that are the same
everywhere — strings, numbers, booleans, plain Java maps, lists and arrays — and each engine's
converter overrides what it needs.

Going from the scope model *into* an engine means presenting a host object as a native one. Rhino
gets `Scriptable` implementations, Graal gets `ProxyObject` / `ProxyArray` / `ProxyExecutable`,
Nashorn gets `AbstractJSObject` subclasses. This is what makes the debug tree, the parameter panels
and the job list work identically whichever engine is running.

A **job** is discovered through this model: any global that is a map with a `main` member is a job,
and `GlobalScope.getJobs()` finds them. See [Writing scripts](scripting.md).

### Threading

Scripts run on a worker thread; the GUI reads the same scope from the event dispatch thread. Rhino
and Nashorn tolerate that. A GraalJS context may only be entered by one thread at a time and every
`Value` stays tied to it, so `GraalRuntime` owns the context and serialises every access through
one lock. It is the engine that needs protecting, not the data, so a single lock is enough.

## The embedded IDE

`pixelbot.components.editor` is a text editor built on Swing's `JEditorPane` with a lexer-driven
document: `ScriptDocument` re-scans the changed paragraph and applies styles from `StyleProvider`.
The lexer is Eclipse JDT's — for Java because that is what it is for, and for JavaScript because
the token grammars overlap closely enough for colouring (see [migration notes](migration.md)).

Around it: a line-number gutter that doubles as the breakpoint margin (`JNumLinePanel`), an
incremental find bar, autocompletion driven by the live scope (`ScriptHint` walks the same
`IScopeMap` tree the debug view shows), drag-and-drop file opening, and a tabbed pane that tracks
which buffers are dirty.

The debugger is Rhino's, wrapped so the rest of the application does not know that: run, suspend,
resume, step into, step over, step return, breakpoints, and a variables tree built from the scope
model.

The log viewer (`pixelbot.components.logviewer`) reads the rolling XML log the application writes
through `java.util.logging`. `BotLogger` redirects `System.out` and `System.err` into the same
place, which is how output from a script ends up in the same window as everything else.

## The application

`PixelBotApp` is the launcher. It builds a class loader covering the jar plus any `lib/` and
`libext/` directories next to it, so compiled scripts and extra engines can be dropped in without
rebuilding, and hands over to `Starter` which sets the look and feel and opens `PixelBotView`.

`PixelBotView` is the main window: the current job and its on/off state, health and mana bars, an
event statistics table, and tabs for the log and the job parameters. The menus open the settings
panels — elements, scripts, interpreters, I/O timing — each of which reads and writes the
preferences under the `/pixelbot` node. `FilePreferencesFactory` redirects that node to a
`pixelbot.fileprefs` file in the working directory, so a setup is a directory you can copy rather
than something buried in the registry.
