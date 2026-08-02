<div align="center">

# 🛒 Walmart Global Tech — Advanced Software Engineering Job Simulation

### End-to-end submission: data structures, system architecture, database design, and data engineering

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.10%2B-3776AB?style=for-the-badge&logo=python&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-3-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![PlantUML](https://img.shields.io/badge/PlantUML-Diagrams-2C3E50?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Submission%20Ready-2ECC71?style=for-the-badge)

</div>

---

## 📖 Overview

This repository contains my complete solution to Walmart Global Tech's **Advanced
Software Engineering Job Simulation** on Forage — four independent tasks spanning data
structure design, object-oriented system architecture, relational database modeling, and
practical data engineering. Every task was built to production standards: analyzed before
implementation, reviewed critically after, and verified rather than assumed correct.

<div align="center">

| # | Task | Focus | Stack |
|:---:|---|---|:---:|
| 1 | [Power-of-Two Max Heap](#-task-1--power-of-two-max-heap) | Data structures & algorithms | ![Java](https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white) |
| 2 | [Reconfigurable Pipeline Architecture](#-task-2--reconfigurable-pipeline-architecture) | OOP design, SOLID, design patterns | ![UML](https://img.shields.io/badge/UML-2C3E50?logo=uml&logoColor=white) |
| 3 | [Pet Department Database Design](#-task-3--pet-department-database-design) | Relational modeling, normalization | ![SQL](https://img.shields.io/badge/SQL-4479A1?logo=postgresql&logoColor=white) |
| 4 | [Shipping Data Populator](#-task-4--shipping-data-populator) | ETL, data aggregation, SQLite | ![Python](https://img.shields.io/badge/Python-3776AB?logo=python&logoColor=white) |

</div>

---

## 🧱 Task 1 — Power-of-Two Max Heap

![Java](https://img.shields.io/badge/Language-Java%2017-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Complexity](https://img.shields.io/badge/insert-O(log__b%20n)-blue?style=flat-square)
![Complexity](https://img.shields.io/badge/popMax-O(b·log__b%20n)-blue?style=flat-square)
![Tests](https://img.shields.io/badge/assertions-90%20passing-2ECC71?style=flat-square)

A max-heap generalized so every node has `2^k` children instead of 2, implemented with
primitive `int[]` storage for zero autoboxing overhead. Includes a from-scratch derivation
of the parent/child index formulas, a caught-and-fixed `int` overflow bug in the
child-range computation at extreme branching exponents, and a 90-assertion test suite
covering binary/wide/degenerate branching factors, boundary conditions, and adversarial
inputs.

📁 `Task 1/` — `PowerOfTwoMaxHeap.java` · `Main.java` · `README.md`

---

## 🏗️ Task 2 — Reconfigurable Pipeline Architecture

![Design](https://img.shields.io/badge/Pattern-Strategy%20%2B%20Registry-8E44AD?style=flat-square)
![Principles](https://img.shields.io/badge/SOLID-Compliant-2ECC71?style=flat-square)
![Diagram](https://img.shields.io/badge/UML-Class%20Diagram-2C3E50?style=flat-square)

Object-oriented architecture for a runtime-reconfigurable data pipeline supporting
pluggable processing modes (Dump / Passthrough / Validate) and pluggable databases
(PostgreSQL / Redis / Elasticsearch). Built on the **Strategy pattern** for both axes of
variation, with **Registry-based dispatch** replacing every `switch`/`if-else` chain —
new modes or databases are added as new classes with zero edits to existing code
(Open/Closed by construction, not by convention).

📁 `Task 2/` — `ARCHITECTURE.md` · `data_processor_class_diagram.html` / `.pdf`

---

## 🗄️ Task 3 — Pet Department Database Design

![Normal Form](https://img.shields.io/badge/Normalization-3NF-2ECC71?style=flat-square)
![Tables](https://img.shields.io/badge/Tables-13-4479A1?style=flat-square)
![Pattern](https://img.shields.io/badge/Pattern-Supertype%2FSubtype-8E44AD?style=flat-square)

A fully normalized (3NF) relational schema for Walmart's Pet Department, covering
products (with a supertype/subtype design for Food/Toy/Apparel to avoid nullable-column
sprawl), manufacturers, animals, customers, transactions, locations, and shipments —
including three associative entities for the schema's genuine many-to-many
relationships. Every design decision (surrogate vs. natural keys, junction tables,
role-based foreign keys for shipment origin/destination) is justified against the stated
business requirements, not assumed.

📁 `Task 3/` — `PET_DEPARTMENT_DATABASE_DESIGN.md` · `ERD.png` / `.pdf`

---

## 🚚 Task 4 — Shipping Data Populator

![Python](https://img.shields.io/badge/Python-Stdlib%20Only-3776AB?style=flat-square&logo=python&logoColor=white)
![DB](https://img.shields.io/badge/SQLite-Populated-003B57?style=flat-square&logo=sqlite&logoColor=white)
![Verified](https://img.shields.io/badge/Products-45%20deduplicated-2ECC71?style=flat-square)
![Verified](https://img.shields.io/badge/Shipments-154%20inserted-2ECC71?style=flat-square)

A Python ETL script that normalizes three CSV files with **different schemas** into a
single SQLite database. `shipping_data_0.csv` is already normalized and inserted
directly; `shipping_data_1.csv` and `shipping_data_2.csv` are joined and aggregated —
one file gives per-item rows that must be grouped and counted with `collections.Counter`,
the other supplies origin/destination metadata keyed by shipment ID. Product
deduplication is handled with an in-memory cache to avoid redundant inserts and keep
foreign keys consistent. Uses the Python standard library only (`sqlite3`, `csv`,
`pathlib`, `collections`, `typing`) — no external dependencies.

**Result:** 45 unique products, 154 shipments inserted, all foreign keys and quantities
verified against the source data.

📁 `Task 4/` — `populate_database.py` · `shipment_database.db` · `data/*.csv`

---

## 🛠️ Engineering Practices Applied Throughout

<div align="center">

| Practice | Where |
|---|---|
| ✅ Requirements analyzed *before* design | All four tasks |
| ✅ Design justified against SOLID / normalization theory, not just implemented | Tasks 2 & 3 |
| ✅ Code compiled and tests actually executed, not just written | Task 1 |
| ✅ Output data verified against source with SQL, not assumed correct | Task 4 |
| ✅ Critical self-review pass performed on every deliverable | All four tasks |

</div>

---

<div align="center">

**Author:** Shah Mubarak Zaib · Final-year BS Computer Science, Islamia College Peshawar
Built for Walmart Global Tech's Advanced Software Engineering Job Simulation (Forage)

</div>
