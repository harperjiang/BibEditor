#!/bin/sh

USITE_REPO=/home/harper/git/TeXDojo/TeXDojoUpdateSite
WEBSITE_REPO=/home/harper/git/website-personal/WebContent/texdojo/updatesite

#USITE_REPO=/home/Cathy/c/workspace/TeXDojo/TeXDojoUpdateSite
#WEBSITE_REPO=/home/Cathy/c/workspace/website-personal/WebContent/bibeditor/updatesite

cd $WEBSITE_REPO
git stash
git pull

rm -rf $WEBSITE_REPO/features
rm -rf $WEBSITE_REPO/plugins
rm -rf $WEBSITE_REPO/artifacts.jar
rm -rf $WEBSITE_REPO/content.jar
rm -rf $WEBSITE_REPO/index.html
rm -rf $WEBSITE_REPO/site.xml

cp -r $USITE_REPO/features $WEBSITE_REPO/
cp -r $USITE_REPO/plugins $WEBSITE_REPO/
cp -r $USITE_REPO/artifacts.jar $WEBSITE_REPO/
cp -r $USITE_REPO/content.jar $WEBSITE_REPO/
cp -r $USITE_REPO/index.html $WEBSITE_REPO/
cp -r $USITE_REPO/site.xml $WEBSITE_REPO/

git add -A .
git commit -m "Daily Update Site"
git push origin master

ssh hajiang@linux1.cs.uchicago.edu -t ". ~/update_site.sh"
