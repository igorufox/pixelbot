# PixelBot

*[Русская версия](README.ru.md)*

A desktop screen-automation toolkit, written in 2011–2014 and brought forward to build and run on a
current JDK.

It watches the screen, finds things on it by matching pixel templates, drives the mouse and
keyboard, and runs your automation scripts from an IDE built into the application — editor with
syntax colouring, breakpoints, a stepping debugger and a log viewer. It also contains a solver for
six-tile jigsaw captchas, which turned out to be the most interesting part of the codebase.

The original was written to automate a browser game. All of that game's data has been removed: the
pixel templates, the map, the artwork. What is left is the machinery, which was never
game-specific, plus the scripts as worked examples of what the engine can be told to do.

## Requirements

* JDK 21 or newer
* Maven 3.8 or newer

## Build and run

```bash
mvn package
```

That produces `target/pixelbot.jar`, a self-contained executable jar. The application reads its
working directory for `scripts/`, `elements.elz` and its preferences file, so run it from a
directory you have set up rather than from `target/`:

```bash
mkdir -p run/scripts && cp examples/scripts/* run/scripts/ && cp examples/pixelbot.fileprefs run/
cd run && java -jar ../target/pixelbot.jar
```

The jar bundles GraalJS, which is most of its size. For a build about half as large, with Rhino and
Nashorn only:

```bash
mvn package -P '!graal'
```

## Documentation

| | |
| --- | --- |
| [Architecture](docs/architecture.md) | Element recognition, the script service layers, the scope model, the embedded IDE |
| [Writing scripts](docs/scripting.md) | The script API, how a job is structured, which engine to pick |
| [The captcha solver](docs/captcha.md) | The scorers, the search, and why the problem reduces to a scoring function |
| [Migration notes](docs/migration.md) | Everything that changed between the 2014 archives and this repository |

## Layout

```
src/main/java/pixelbot/
    captcha/      tile captcha: scorers, solvers, the search
    components/   reusable Swing widgets, the editor, the log viewer
    elements/     pixel templates and their storage
    gui/          the application window and its panels
    script/       parser / instantiator / scope / debugger service layers
src/test/java/    tests for the captcha algorithms and the script backends
examples/scripts/ the original automation scripts, kept as examples
docs/             the documents linked above
tools/            the icon generator
```

## Licence

MIT — see [LICENSE](LICENSE). Two SwingX localisation bundles kept in the source tree stay under
SwingX's own LGPL 2.1; [NOTICE](NOTICE) records that, and it is the only third-party content here.

## A note on what this is for

This automates a desktop application by looking at the screen and moving the mouse. It has no
network code and knows nothing about any particular program. Whether pointing it at something is
allowed is between you and whatever you point it at; many online services forbid it in their terms.

Scripts run with full access to the host — that is the point of the Java and JavaScript backends —
so only run scripts you wrote or have read.
