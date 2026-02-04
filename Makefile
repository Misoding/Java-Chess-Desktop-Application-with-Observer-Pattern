JAVAC       := javac
JAVA        := java

SRC_DIR     := src
OUT_DIR     := out
LIB_DIR     := lib

JSON_LIB    := $(LIB_DIR)/json-simple-1.1.1.jar

MAIN_CLASS  := main_package.Main
TEST_CLASS  := Testare.ChessTestRunner

JAVAC_FLAGS := -encoding UTF-8 -Xlint:unchecked -Xlint:deprecation

ifeq ($(OS),Windows_NT)
    PATHSEP := ;
    FIND_SOURCES := $(shell dir /s /b $(SRC_DIR)\*.java 2>nul)
else
    PATHSEP := :
    FIND_SOURCES := $(shell find $(SRC_DIR) -name '*.java')
endif

COMPILE_CP  := $(JSON_LIB)
RUNTIME_CP  := $(OUT_DIR)$(PATHSEP)$(JSON_LIB)

.PHONY: all
all: compile

.PHONY: help
help:
	@echo "=========================================="
	@echo " Chess Application - Build Targets"
	@echo "=========================================="
	@echo ""
	@echo "  make all       Compile the project (default)"
	@echo "  make compile   Compile all Java sources"
	@echo "  make run       Run the GUI application"
	@echo "  make test      Run the console test harness"
	@echo "  make clean     Remove compiled artifacts"
	@echo "  make help      Show this message"
	@echo ""
	@echo "Requirements:"
	@echo "  - JDK 8 or higher"
	@echo "  - GNU Make"
	@echo ""

.PHONY: compile
compile: $(OUT_DIR)
	@echo "[COMPILE] Building Java sources..."
ifeq ($(OS),Windows_NT)
	@$(JAVAC) $(JAVAC_FLAGS) -d $(OUT_DIR) -cp "$(COMPILE_CP)" -sourcepath $(SRC_DIR) $(SRC_DIR)/main_package/Main.java $(SRC_DIR)/Testare/ChessTestRunner.java
else
	@$(JAVAC) $(JAVAC_FLAGS) -d $(OUT_DIR) -cp "$(COMPILE_CP)" -sourcepath $(SRC_DIR) $(FIND_SOURCES)
endif
	@echo "[COMPILE] Done. Output: $(OUT_DIR)/"

$(OUT_DIR):
	@mkdir -p $(OUT_DIR) 2>/dev/null || mkdir $(OUT_DIR)

.PHONY: run
run: compile
	@echo "[RUN] Starting Chess GUI..."
	@$(JAVA) -cp "$(RUNTIME_CP)" $(MAIN_CLASS)

.PHONY: test
test: compile
	@echo "[TEST] Starting Console Test Runner..."
	@$(JAVA) -cp "$(RUNTIME_CP)" $(TEST_CLASS)

.PHONY: clean
clean:
	@echo "[CLEAN] Removing compiled artifacts..."
ifeq ($(OS),Windows_NT)
	@if exist $(OUT_DIR) rmdir /s /q $(OUT_DIR)
else
	@rm -rf $(OUT_DIR)
endif
	@echo "[CLEAN] Done."

.PHONY: check-env
check-env:
	@echo "Checking build environment..."
	@$(JAVAC) -version
	@$(JAVA) -version
	@echo "JSON Library: $(JSON_LIB)"
ifeq ($(OS),Windows_NT)
	@if exist $(JSON_LIB) (echo "  [OK] Found") else (echo "  [ERROR] Missing!")
else
	@test -f $(JSON_LIB) && echo "  [OK] Found" || echo "  [ERROR] Missing!"
endif
