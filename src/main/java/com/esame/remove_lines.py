with open("combine.txt", "r") as f:
    lines = f.readlines()

new_lines = []

docs = False
for line in lines:
    if "/**" in line:
        docs = True
        continue
    elif "*/" in line:
        docs = False
        continue
    elif docs:
        continue
    elif line == "\n":
        continue
    else:
        new_lines.append(line)

with open("combine_no_lines.txt", "w") as f:
    f.writelines(new_lines)