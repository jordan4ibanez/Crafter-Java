# Crafter - Pre-Classic (Prototyping) - 0.0.4
#### A blocky game (engine) written in Java with LWJGL.
[![CI](https://github.com/jordan4ibanez/Crafter-Java/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/jordan4ibanez/Crafter-Java/actions/workflows/gradle-ci.yml)
[![Package Release](https://github.com/jordan4ibanez/Crafter-Java/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/jordan4ibanez/Crafter-Java/actions/workflows/gradle-publish.yml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)


<a href = https://discord.gg/dRPyvubfyg> <img src="https://assets-global.website-files.com/6257adef93867e50d84d30e2/636e0a69f118df70ad7828d4_icon_clyde_blurple_RGB.svg" width="27" height="15" alt="Discord Logo"> 
<font>- Discord</font>
</a>

[![Matrix Space](https://matrix.org/images/matrix-logo-white.svg)](https://matrix.to/#/#crafter:matrix.org)

### [View random, sporadic updates on my Youtube by clicking this text.](https://www.youtube.com/@Jordan4Ibanez)

## Project code direction: Modified Unix Philosophy
**Note:** Code Philosophy should be in its own file in the future. It is fine here for now though. 
### Main gist
#### Rob Pike (algorithms & containers)
1. You can't tell where a program is going to spend its time. Bottlenecks occur in surprising places, so don't try to second guess and put in a speed hack until you've proven that's where the bottleneck is.
2. Measure. Don't tune for speed until you've measured, and even then don't unless one part of the code overwhelms the rest.
3. Fancy algorithms are slow when n is small, and n is usually small. Fancy algorithms have big constants. Until you know that n is frequently going to be big, don't get fancy. (Even if n does get big, use Rule 2 first.)
4. Fancy algorithms are buggier than simple ones, and they're much harder to implement. Use simple algorithms as well as simple data structures.
5. Data dominates. If you've chosen the right data structures and organized things well, the algorithms will almost always be self-evident. Data structures, not algorithms, are central to programming
6. Each class is a program
#### Doug McIlroy (general architectural design principals)
1. Make each program do one thing well. To do a new job, build afresh rather than complicate old programs by adding new "features".
2. Expect the output of every program to become the input to another, as yet unknown, program. Don't clutter output with extraneous information. Avoid stringently columnar or binary input formats. Don't insist on interactive input.
3. Design and build software, even operating systems, to be tried early, ideally within weeks. Don't hesitate to throw away the clumsy parts and rebuild them.
4. Use tools in preference to unskilled help to lighten a programming task, even if you have to detour to build the tools and expect to throw some of them out after you've finished using them.

### Philosophy translation to project
1. Have fun.
2. Write clean code. It should be easy to read. It should be explicit. Use comments where needed.
3. Simplicity is your friend. It does not matter if it is not the fastest thing in the world. If it is the fastest algorithm on the JVM and we cannot maintain it, it is useless.
4. Each class is treated as a program. It has its job, and it will do it well. Do not juggle class features. A class that does 5+ things at once, for example, is very bad.
5. Each class must be as loosely coupled as possible. If tight coupling occurs, perhaps a manager class must be created to handle it.
6. If a class is becoming overly complex, rewrite it or break it down into a set of smaller classes. (IF possible)
7. No nested classes. Nested classes can cause severe complications. Prefer to utilize internal package classes.
8. Do not cause inheritance hell. Only use inheritance if necessary. (GUI, Entities, etc)
9. If there are multiple instances of a class, the preferred method of storage is a SEPARATE storage class for simplicity.
10. OOP is not the bible. You can treat a class as pure static. If a class is pure static it has three rules:
    1. It must be a final class.
    2. It must have a private blank constructor.
    3. If it is accessed from multiple threads, it MUST utilize ``synchronized`` in the methods that get called from those threads.
11. Have fun.