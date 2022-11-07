@echo off

cd .\src\main\webapp

echo Clean js folder..
del /Q .\js\*.*

echo Compile res-en...
call tsc --noEmitHelpers --declaration --project ./ts/res-en/tsconfig.json
echo Compile res-it...
call tsc --noEmitHelpers --declaration --project ./ts/res-it/tsconfig.json
echo Compile res-fr...
call tsc --noEmitHelpers --declaration --project ./ts/res-fr/tsconfig.json
echo Compile res-es...
call tsc --noEmitHelpers --declaration --project ./ts/res-es/tsconfig.json
echo Compile ljsa...
call tsc --noEmitHelpers --declaration --project ./ts/ljsa/tsconfig.json

rem Install first https://www.npmjs.com/package/minifier
echo Minify...
call minify ./js/res-en.js
call minify ./js/res-it.js
call minify ./js/res-fr.js
call minify ./js/res-es.js
call minify ./js/ljsa.js

rem Install first https://www.npmjs.com/package/uglify-js
rem Usage: uglifyjs input_file -c (compress) -o (output_file) output_file
rem call uglifyjs ./js/res-en.js -c -o ./js/res-en.min.js
rem call uglifyjs ./js/res-it.js -c -o ./js/res-it.min.js
rem call uglifyjs ./js/res-fr.js -c -o ./js/res-fr.min.js
rem call uglifyjs ./js/res-es.js -c -o ./js/res-es.min.js
rem call uglifyjs ./js/ljsa.js -c -o ./js/ljsa.min.js
