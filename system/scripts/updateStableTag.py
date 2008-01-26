#!/usr/bin/python
import os
import sys
import random
import optparse
import httplib
import getpass
import base64

def updateStable(baseRepoURL):
	trunkDir="trunk/";
	versionDir="tags/versions/";
	branchDir="branches/";

	versionURL=baseRepoURL+versionDir+"stable";
	trunkURL=baseRepoURL+trunkDir;



	cleanCommand="ant clean";
	buildAllCommand="ant all";
	commitCommand="svn commit -m 'committing latest changes'";
	removeStableVersionCommand="svn rm "+versionURL+" -m 'removing the stable tag'";
	tagVersionCommand="svn cp "+trunkURL+" "+versionURL+" -m 'Updating the stable tag'";


	Commands=[cleanCommand, buildAllCommand,commitCommand,removeStableVersionCommand,tagVersionCommand];
	
	

	print("\n-------------------------------\Executing the following :\n");
	for c in Commands:
		status=os.system(c);
		print "Status: "+str(status)+" : "+c;
		if(status):
			print("Something bad happened, aborting!");
			sys.exit();
		
	return;
	

def main():
	baseRepoURL="https://rl-viz.googlecode.com/svn/";
	updateStable(baseRepoURL);

if __name__ == '__main__':
  main()