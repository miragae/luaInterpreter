# Lua Interpreter
Aplikacja wykorzystuje wygenerowany parser do interpretowania skryptów napisanych w języku Lua.


Do wygenerowania parsera zadanej gramatyki została użyta biblioteka `antlr4`. 
\
Pliki opisujące gramatykę umieszczone zostały w plikach `Lua.g4` i `LuaTokens.g4` pod ścieżką `src\main\antlr4`.


## 0. Wymagania

Upewnij się, że masz zainstalowane:

`jdk15` `maven`

## 1. Instalacja i uruchomienie

Sklonuj repozytorium

`git clone https://github.com/miragae/luaInterpreter`

Zbuduj projekt

`cd luaInterpreter`\
`mvn install`

Uruchom aplikację podając jako argument ścieżkę do pliku ze skryptem Lua do zinterpretowania, np.

`java -jar target/LuaInterpreter-1.0-jar-with-dependencies.jar src/main/resources/helloworld.lua`

## 2. Przykłady

Pod ścieżką `src/main/resources` są zamieszczone przykładowe skrypty Lua do przetestowania projektu:

`helloworld.lua` - wypisuje tekst "Hello World"\
`function.lua` - testuje wykorzystanie prostej funkcji, zasięg zmiennych oraz operacje na liczbach\
`factorial.lua` - oblicza silnię - testuje rekurencję, stos funkcji, wprowadzanie danych przez użytkownika\
`tables.lua` - testuje inicjalizacje i operacje na tablicach i ich danych\
`loops.lua` - testuje pętle