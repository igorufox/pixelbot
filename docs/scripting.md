# Writing scripts

*[Русская версия](scripting.ru.md)*

A script is a file in the `scripts/` directory next to the application. It is loaded when the
application starts, evaluated, and whatever it defines at the top level becomes visible to the rest
of the program.

| Extension | Language | Run by |
| --- | --- | --- |
| `.djs` | JavaScript | Rhino, GraalJS or Nashorn — your choice, per file type |
| `.djava` | Java | Compiled in process by the Eclipse batch compiler |
| `.jar`, `.zip`, `.class` | | Loaded as already-compiled code |

Which engine runs `.djs` is set in *Settings → Interpreters*. See
[the engine list](architecture.md#parsers).

## A job

Any global object with a `main` member is a **job** — it shows up in the job dropdown in the main
window and can be started and stopped from there. Give it `params` as well and the application
builds a parameters form for it.

```javascript
Wand = new function() {};
Wand.prototype = {};

Wand.params = {
    name: 'Magic wand',

    // The form shown in the Parameters tab.
    config: function(params) {
        return [
            { name: 'type',        label: 'Type',   type: 'radio', values: ['a', 'b'] },
            { name: 'config_file', label: 'Config', type: 'file' }
        ];
    },

    defaults: {
        type: 'a',
        config_file: 'private/config.xlsx'
    }
};

Wand.main = function(params) {
    PixelBot.log.info('starting with ' + params['type']);

    while (PixelBot.grab_find_click('interface.buttons.close', 1)) {
        PixelBot.sleep(2000);
    }
};
```

`main` receives the parameter values the user entered. `params.config` may be a plain array instead
of a function if the form does not depend on anything. An `active` member, either a boolean or a
function returning one, decides whether the job is offered at all.

`examples/scripts/` holds the original jobs this was written for — `wand.djs` is the smallest
complete one, `hhtools.djs` the largest.

## The library

`examples/scripts/pixelbot.djs` is not a job; it is the library every other script uses. It wraps
`Helper`, the Java object the application injects into the scope, in something more convenient. Copy
it into your `scripts/` directory alongside your own.

The basic loop is always the same: **grab the screen, find an element, move to it, click**.

```javascript
PixelBot.grab();                              // capture the whole screen
if (PixelBot.find('interface.buttons.ok')) {  // look for a template
    PixelBot.move_elem();                     // move somewhere inside the match
    PixelBot.click(1);
}
```

Almost every combination of those has a shorthand, which is what the long list of names is:

```javascript
PixelBot.grab_find_click('interface.buttons.ok', 1);
PixelBot.grab_find_click_rect('interface.buttons.ok', 1, x, y, width, height);
```

The `_rect` variants restrict both the grab and the search to a rectangle, which is much faster than
scanning the whole screen and is what you want inside a loop.

### What is available

| Group | |
| --- | --- |
| Screen | `grab`, `grab_rect`, `highlight_region`, `screen` |
| Find | `find`, `find_rect`, `find_many`, `find_many_rect`, `get_elements` |
| Combined | `find_click`, `find_move`, `grab_find`, `grab_find_click`, and `_rect` forms of each |
| Mouse | `move`, `move_rel`, `move_click`, `move_rel_click`, `move_elem`, `click`, `mouse.press`, `mouse.release` |
| Keyboard | `key.click`, `key.press`, `key.release`, and the `key.VK_*` constants |
| Colour | `square`, `grab_square` — test a rectangle against a colour range |
| Captcha | `solveCaptcha` — see [the captcha solver](captcha.md#using-it) |
| Misc | `sleep`, `log.{trace,info,warning,error,fatal,success}`, `state`, `statistics`, `sound`, `link`, `browser` |

`last_find_rect` holds the last match, which is what `move_rel` and `move_elem` work from.
`move_elem` picks a random point inside the match rather than its centre — like the randomised mouse
timing, that is deliberate.

Anything the library does not wrap is reachable through `Helper` directly, and Java classes through
`Packages.java.…` in Rhino and Nashorn.

## Elements

Scripts refer to elements by name. The templates themselves are captured in *Settings → Elements*:
grab a region of the screen, mark the part that matters, name it, save. Names are dot-separated by
convention, so `interface.buttons.close` groups with its siblings in the tree, and
`get_elements(category)` returns a whole category at once.

No templates ship with this repository — they were the previous target's artwork. You capture your
own for whatever you are automating.

## Choosing an engine

**GraalJS** for anything new: current ECMAScript, actively developed, fast.

**Rhino in debug mode** when you want the stepping debugger — breakpoints in the gutter, step into
and over, and a variables tree over the live scope. Only this backend supports it.

**Rhino in compilation mode** for the same language, compiled to bytecode, without the debugger.

**Nashorn** if you are running scripts written for the Java 8 era and something in them depends on
how that engine behaved.

The scripts in `examples/` were written against Rhino in 2014. They run on the other engines too,
but they are old-style JavaScript and do not exercise anything modern.

## Debugging

Open a script in the editor tab, click in the line-number margin to set a breakpoint, and run the
job with the debug button. When it stops, the variables tree shows the live scope through the same
model the rest of the application uses, so you see the actual objects your script defined rather
than a serialised copy.

`PixelBot.log.*` writes to the log tab, and so does anything a script prints to standard output —
`BotLogger` redirects both streams into the logging subsystem.

## A caution

Scripts run with full access to the host: that is the entire point of the Java backend and of
`Packages.java.…`. Only run scripts you wrote or have read.
