# bootfxui

Using JavaFX directly via Java interop with boot build tool.

Boot does exit the application properly after closing the (last) GUI window. This is an issue
encountered when using Leiningen instead.

## Issues

REPL does not work well yet. REPL does not recognize the `gen-class` generated class
needed to launch the application.

## License

Copyright © 2017 Dr. Nils Blum-Oeste

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
