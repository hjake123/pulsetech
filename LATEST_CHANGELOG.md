- Added the 'hide' keyword, which makes certain macros invisible to the Program Emitter
- Fixed a crash when running alongside Alternate Current
- Fixed a hang when using macros that self-recurse excessively
- Made the Program Emitter's macros sort in a consistent order to prevent the required signal changing unexpectedly
- Added a config value to adjust the number of times macros can unwrap in a single evaluation 
- Allowed all signal emitting blocks to strongly power blocks they are directly facing, similarly to Repeaters
- Made the Pattern Emitter GUI have multiple lines of input to avoid it going off screen, and added a max length for it of 24 bits

Special thanks to chipsams and XDuskAshes on GitHub for helping find these issues!