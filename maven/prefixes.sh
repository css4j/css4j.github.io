#!/bin/bash
# Run INSIDE the /maven/ directory

OUTPUT_DIR=".meta"
OUTPUT_FILE="${OUTPUT_DIR}/prefixes.txt"

mkdir -p "$OUTPUT_DIR"

# 1. Mandatory header required by Maven Resolver
echo "## repository-prefixes/2.0" > "$OUTPUT_FILE"

# 2. Find .pom files and extract only the groupId directory path
find . -type f -name "*.pom" ! -path "./${OUTPUT_DIR}/*" | awk -F'/' '{
    # Typical directory structure: . / org / group / my-artifact / 1.0.0 / my-artifact-1.0.0.pom
    # Total path parts (NF) = 7
    #  $1 = .
    #  $2..$(NF-3) = groupId (/org/group)
    #  $(NF-2) = artifactId (my-artifact)
    #  $(NF-1) = version (1.0.0)
    #  $NF     = filename (.pom)

    if (NF >= 5) {
        group_path = "";
        for (i = 2; i <= NF - 3; i++) {
            group_path = group_path "/" $i;
        }
        if (group_path != "") {
            print group_path;
        }
    }
}' | sort -u >> "$OUTPUT_FILE"
