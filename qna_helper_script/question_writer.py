import json
import re
import sys
from pathlib import Path
from typing import List, Dict, Any

import yaml


def generate_filename_from_text(text: str, max_chars: int = 20) -> str:
    """
    Generate filename from first N characters of question text.

    - Takes first max_chars characters
    - Converts all whitespace to underscores
    - Removes filesystem-unfriendly characters
    """
    truncated = text[:max_chars]

    # Replace any whitespace (spaces, tabs, newlines) with underscores
    filename = re.sub(r"\s+", "_", truncated)

    # Keep only alphanumeric, underscore, and dash
    filename = re.sub(r"[^A-Za-z0-9_-]", "", filename)

    filename = filename.strip("_")

    if not filename:
        filename = "question"

    return f"{filename}.yaml"


def load_questions_json(json_file: str) -> List[Dict[str, Any]]:
    """Load questions from JSON file."""
    try:
        with open(json_file, "r", encoding="utf-8") as f:
            data = json.load(f)

        if isinstance(data, list):
            return data

        if isinstance(data, dict) and "questions" in data:
            return data["questions"]

        print(
            "Error: JSON must contain either:\n"
            "  - a list of questions\n"
            "  - {'questions': [...]}"
        )
        sys.exit(1)

    except FileNotFoundError:
        print(f"Error: File not found: {json_file}")
        sys.exit(1)

    except json.JSONDecodeError as e:
        print(f"Error: Invalid JSON: {e}")
        sys.exit(1)


def validate_question(question: Dict[str, Any], index: int) -> bool:
    """Validate required fields."""

    required = [
        "category",
        "topic",
        "text",
        "options",
        "answers",
        "reasoning",
        "difficulty",
    ]

    missing = [field for field in required if field not in question]

    if missing:
        print(f"Warning: Question {index} missing fields: {missing}")
        return False

    options = question.get("options")

    if not isinstance(options, list) or len(options) != 4:
        print(f"Warning: Question {index} options must contain exactly 4 items")
        return False

    answers = question.get("answers")

    if not isinstance(answers, list) or not answers:
        print(f"Warning: Question {index} answers must be a non-empty array")
        return False

    for answer in answers:
        if not isinstance(answer, int) or answer < 1 or answer > 4:
            print(
                f"Warning: Question {index} answer values "
                f"must be integers between 1 and 4"
            )
            return False

    return True


def generate_yaml_content(question: Dict[str, Any]) -> str:
    """
    Generate YAML content using PyYAML.

    PyYAML safely handles:
    - multiline strings
    - quotes
    - escaping
    - unicode
    """

    data = {
        "version": 1,
        "text": question["text"],
        "options": question["options"],
        "answers": question["answers"],
        "reasoning": question["reasoning"],
        "difficulty": question["difficulty"],
    }

    return yaml.safe_dump(
        data,
        allow_unicode=True,
        sort_keys=False,
        default_flow_style=False,
        width=1000,
    )


def create_directory_structure(
    output_root: str,
    category: str,
    topic: str,
) -> Path:
    """Create category/topic directory structure."""
    folder = Path(output_root) / category / topic
    folder.mkdir(parents=True, exist_ok=True)
    return folder


def generate_questions(
    json_file: str,
    output_root: str = "questions",
) -> None:
    """Main generator."""

    print(f"Loading questions from: {json_file}")

    questions = load_questions_json(json_file)

    print(f"Total questions loaded: {len(questions)}")

    categories_created = {}
    valid_count = 0
    skipped_count = 0

    for index, question in enumerate(questions, start=1):

        if not validate_question(question, index):
            skipped_count += 1
            continue

        category = str(question["category"]).strip()
        topic = str(question["topic"]).strip()

        folder_path = create_directory_structure(
            output_root,
            category,
            topic,
        )

        categories_created.setdefault(category, {})
        categories_created[category].setdefault(topic, 0)
        categories_created[category][topic] += 1

        filename = generate_filename_from_text(question["text"])

        file_path = folder_path / filename

        base_name = file_path.stem
        counter = 1

        while file_path.exists():
            file_path = folder_path / f"{base_name}_{counter}.yaml"
            counter += 1

        yaml_content = generate_yaml_content(question)

        with open(file_path, "w", encoding="utf-8") as f:
            f.write(yaml_content)

        print(f"✓ Created: {file_path.relative_to(output_root)}")

        valid_count += 1

    print("\n" + "=" * 60)
    print("GENERATION COMPLETE")
    print("=" * 60)

    print(f"Valid questions created: {valid_count}")
    print(f"Skipped: {skipped_count}")

    print("\nDistribution by category/topic:")

    for category in sorted(categories_created):
        print(f"  [{category}]")

        for topic in sorted(categories_created[category]):
            count = categories_created[category][topic]
            print(f"    └─ {topic}: {count} questions")

    print("\n--- Folder Structure ---")

    output_path = Path(output_root)

    if output_path.exists():
        for item in sorted(output_path.rglob("*")):
            if item.is_dir():
                depth = len(item.relative_to(output_root).parts)
                indent = "  " * depth
                print(f"{indent}├─ {item.name}/")

    sample_files = list(output_path.rglob("*.yaml"))

    if sample_files:
        sample = sample_files[0]

        print("\n--- Sample File Content ---")
        print(f"Path: {sample.relative_to(output_root)}")
        print()

        with open(sample, "r", encoding="utf-8") as f:
            print(f.read())

    print(f"\n✓ All files generated in: {output_path.absolute()}")


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(
        description="Generate question YAML files from JSON"
    )

    parser.add_argument(
        "json_file",
        help="Input JSON file containing questions",
    )

    parser.add_argument(
        "-o",
        "--output",
        default="questions",
        help="Output root folder (default: questions)",
    )

    args = parser.parse_args()

    generate_questions(
        json_file=args.json_file,
        output_root=args.output,
    )

