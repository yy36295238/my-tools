#!/bin/bash

WEB_TOOL_PATH=/Users/yangyu/workspace/yyself/my-tools-web
WEB_TOOL_DIST_PATH=/Users/yangyu/workspace/yyself/my-tools-web/dist
STATIC_PATH=/Users/yangyu/workspace/yyself/my-tools/my-tools-web/src/main/resources/static

cd $WEB_TOOL_PATH

npm run build

cd $WEB_TOOL_DIST_PATH

cp -r * $STATIC_PATH
