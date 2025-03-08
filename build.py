import os
import re
import shutil
import subprocess

from pythonclimenu import menu


def checkVersion(command: str) -> int:
    if not shutil.which(command):
        return 0

    strVer = subprocess.run(
        [command, "--version"],
        capture_output=True,
        text=True,
        stderr=None,
    ).stdout

    return int(re.search(r"\s(\d+).", strVer).group(1))


def build(module: str, javaVer: int) -> None:
    if os.path.exists("out/"):
        shutil.rmtree("out/")

    shutil.copytree(f"modules/{module}/", "out/")
    os.chdir("out/")

    if not os.path.exists("MANIFEST.MF"):
        raise Exception(f"Failed to locate MANIFEST.MF for module {module}.")

    with open("MANIFEST.MF", "r") as f:
        className = ""
        for line in f.readlines():
            if line.strip().startswith("Main-Class"):
                className = line.split(":")[1].strip()

        if className == "":
            raise Exception(f"Could not find Main-Class in MANIFEST.MF for module {module}.")

    subprocess.run(["javac", "--release", str(javaVer), f"{className}.java"], capture_output=True)
    subprocess.run(["jar", "cvfm", "server.jar", "MANIFEST.MF", f"{className}.class"], capture_output=True)


def on_build(target: str) -> None:
    if target in modules:
        build(target, checkVersion("javac"))
        print("Built successfully to out/server.jar")
    else:
        raise Exception(f"Target '{target}' not found.")


if __name__ == "__main__":
    for cmd in ["javac", "jar"]:
        v = checkVersion(cmd)
        if v < 21:
            raise Exception(f"Could not find {cmd} or version too low (found {v}).")

    modules = os.listdir("modules")
    selected = menu(
        title=["Select Build Target", "Choose a module to build"],
        options=modules,
        cursor_color="blue",
        title_color="green",
    )

    on_build(selected)
