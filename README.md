# JarSH
JarSH is a tool to allow minecraft servers run programs they weren't originally intended to run.

# Usage
1. Clone the repo locally (`git clone https://github.com/meeplabsdev/jarsh.git && cd jarsh`).
2. Install the python requirements (`pip install -r requirements.txt`).
3. Run the build.py script to select a module (`python build.py`).
4. Select a module and it will be built to the `/out` directory. To use it, just replace the normal minecraft server jar with the custom one built.

# Development
1. Follow the usage guide until you have a working terminal application that successfully builds a `server.jar` into `/out`.
2. Make a new directory inside the `/modules`, and copy the `MANIFEST.MF` and `BashServer.java` into it as a starting point.
3. Rename the `.java` file and the class within it, and update the `MANIFEST.MF` to point to this class.
4. Write your module in the `.java` file, and credit yourself in the `MANIFEST.MF` file.
5. Re-run the `build.py` and you should see the custom module there.
6. Feel free to leave a pull request for the module!