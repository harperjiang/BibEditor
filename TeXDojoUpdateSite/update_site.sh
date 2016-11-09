#!/bin/sh

#USITE_REPO=/home/harper/Repositories/BibEditor/BibEditorUpdateSite
#WEBSITE_REPO=/home/harper/Repositories/websites/personal/WebContent/bibeditor/updatesite

USITE_REPO=/home/Cathy/c/workspace/BibEditor/BibEditorUpdateSite
WEBSITE_REPO=/home/Cathy/c/workspace/website-personal/WebContent/bibeditor/updatesite

rm -rf $WEBSITE_REPO/features
rm -rf $WEBSITE_REPO/plugins
rm -rf $WEBSITE_REPO/artifacts.jar
rm -rf $WEBSITE_REPO/content.jar

cp -r $USITE_REPO/features $WEBSITE_REPO/
cp -r $USITE_REPO/plugins $WEBSITE_REPO/
cp -r $USITE_REPO/artifacts.jar $WEBSITE_REPO/
cp -r $USITE_REPO/content.jar $WEBSITE_REPO/

cd $WEBSITE_REPO
git stash
git pull
git add -A .
git commit -m "Daily Update Site"
git push origin master

ssh hajiang@people.cs.uchicago.edu -t ". ~/update_site.sh"
