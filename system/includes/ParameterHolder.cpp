/*
 *  ParameterHolder.cpp
 *  BT-Glue
 *
 *  Created by Brian Tanner on 09/04/07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "ParameterHolder.h"

#include <iostream>
#include <sstream>

int main(){
    	ParameterHolder P;
	P.addIntParam("sampleIntParam",5);
	P.addDoubleParam("sampleDoubleParam", 2.1);
	P.addBoolParam("sampleBoolParam", true);
	P.addStringParam("sampleStringParam", "thetest");
	P.setAlias("intParam", "sampleIntParam");
	std::string parameterString=P.stringSerialize();
        std::string javaString = "PARAMHOLDER_4_sampleIntParam_0_5_sampleDoubleParam_1_2.1_sampleBoolParam_2_true_sampleStringParam_3_thetest_5_sampleIntParam_sampleIntParam_sampleDoubleParam_sampleDoubleParam_sampleBoolParam_sampleBoolParam_sampleStringParam_sampleStringParam_intParam_sampleIntParam_";
        if(parameterString == javaString)
            std::cout << "they are equal \n";

        std::cout << "The string is:\n" << parameterString.c_str() << std::endl;
    
        return 0;
}
ParameterHolder::ParameterHolder(const std::string theString){
	 std::istringstream iss(theString);
	 unsigned int numParams;
	 std::string thisParamName;
	
	double fParamValue;
	int iParamValue;
	bool bParamValue;
	std::string sParamValue;

	int tempType;
	PHTypes thisParamType;
	
	//MAke sure the first bit of this isn't NULL!
	std::string pointerType;
	std::getline(iss,pointerType,'_');
	
	if(pointerType=="NULL")
		return;
		
	iss>>numParams;
	iss.ignore();
	 for(size_t i=0;i<numParams;i++){
		std::getline(iss,thisParamName,'_');
		
		iss>>tempType;
             
		thisParamType=(PHTypes)tempType;
		iss.ignore();

		if(thisParamType==intParam){
			iss>>iParamValue;
			iss.ignore();
			addIntParam(thisParamName,iParamValue);
                              
		}
		if(thisParamType==doubleParam){
			iss>>fParamValue;
			iss.ignore();
			addDoubleParam(thisParamName,fParamValue);
		}
		if(thisParamType==boolParam){
                    //Booleans will be "true" or "false"
                 	std::getline(iss,sParamValue,'_');
                        if(sParamValue=="true")
            			addBoolParam(thisParamName,true);
                        else
            			addBoolParam(thisParamName,false);
                            
		}
		if(thisParamType==stringParam){
			std::getline(iss,sParamValue,'_');
			addStringParam(thisParamName,sParamValue);
		}
	}

        //Alias time
	int numAliases;
	iss>>numAliases;
	iss.ignore();
	for(size_t i=0;i<numAliases;i++){
		std::string thisAlias;
		std::string thisTarget;
		std::getline(iss,thisAlias,'_');
		std::getline(iss,thisTarget,'_');
		setAlias(thisAlias,thisTarget);
	}
		
}
std::string ParameterHolder::stringSerialize() {
	std::ostringstream outs;  // Declare an output string stream.
	
//Do this here instead of externally later when we're ready
	outs<<"PARAMHOLDER_";
	//First, write the number of param names
	outs<<allParamNames.size()<<"_";
	for(size_t i=0;i<allParamNames.size();i++){
		outs<<allParamNames[i]<<"_";
		unsigned int paramType=allParamTypes[i];
		outs<<paramType<<"_";
		if(paramType==intParam)outs<<getIntParam(allParamNames[i])<<"_";
		if(paramType==doubleParam)outs<<getDoubleParam(allParamNames[i])<<"_";
                if(paramType==boolParam){
                    bool theValue=getBoolParam(allParamNames[i]);
                    if(theValue)
                        outs<<"true_";
                    else
                        outs<<"false_";
                }
		if(paramType==stringParam)outs<<getStringParam(allParamNames[i])<<"_";
	}
	
	//Now write all of the aliases
	outs<<allAliases.size()<<"_";
	for(size_t i=0;i<allAliases.size();i++)outs<<allAliases[i]<<"_"<<getAlias(allAliases[i])<<"_";

	return outs.str();
}






ParameterHolder::ParameterHolder(){}

ParameterHolder::~ParameterHolder(){}

std::string ParameterHolder::getAlias(std::string alias) {
	if(aliases.count(alias)==0){
		std::cerr<<"You wanted to look up original for alias: "<<alias<<", but that alias hasn't been set"<<std::endl;
		exit(-1);
	}
	return  aliases[alias];
}

void ParameterHolder::setAlias(std::string alias, std::string original){
	if(allParams.count(original)==0){
		std::cerr<<"C++ Parameter Holder::Careful, you are setting an alias of:"<<alias<<" to: "<<original<<" but: "<<original<<" isn't in the parameter set"<<std::endl;
		exit(1);
	}
	aliases[alias]=original;
	allAliases.push_back(alias);
}


void ParameterHolder::addIntParam(std::string name, int defaultValue){
	addIntParam(name);
	setIntParam(name, defaultValue);
}
void ParameterHolder::addDoubleParam(std::string name, double defaultValue){
	addDoubleParam(name);
	setDoubleParam(name, defaultValue);
}
void ParameterHolder::addBoolParam(std::string name, bool defaultValue){
	addBoolParam(name);
	setBoolParam(name, defaultValue);
}
void ParameterHolder::addStringParam(std::string name, std::string defaultValue){
	addStringParam(name);
	setStringParam(name, defaultValue);
}

void ParameterHolder::addIntParam(std::string name){
	allParams[name]=intParam;
	allParamNames.push_back(name);
	allParamTypes.push_back(intParam);
	setAlias(name,name);
}
void ParameterHolder::addDoubleParam(std::string name){
	allParams[name]=doubleParam;
	allParamNames.push_back(name);
	allParamTypes.push_back(doubleParam);
	setAlias(name,name);
}
void ParameterHolder::addBoolParam(std::string name){
	allParams[name]=boolParam;
	allParamNames.push_back(name);
	allParamTypes.push_back(boolParam);
	setAlias(name,name);
}
void ParameterHolder::addStringParam(std::string name){
	allParams[name]=stringParam;
	allParamNames.push_back(name);
	allParamTypes.push_back(stringParam);
	setAlias(name,name);
}


void ParameterHolder::setIntParam(std::string alias, int value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);
	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	intParams[name]=value;
}

void ParameterHolder::setDoubleParam(std::string alias, double value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);
	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	doubleParams[name]=value;
}

void ParameterHolder::setBoolParam(std::string alias, bool value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	boolParams[name]=value;
}

void ParameterHolder::setStringParam(std::string alias, std::string value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	stringParams[name]=value;
}
int ParameterHolder::getIntParam(std::string alias) {
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(intParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't an int parameter...\n"<<std::endl;exit(1);}
	int retVal=intParams[name];
	return retVal;
}
double ParameterHolder::getDoubleParam(std::string alias) {
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(doubleParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't an double parameter...\n"<<std::endl;exit(1);}

	double retVal=doubleParams[name];
	return retVal;
}
bool ParameterHolder::getBoolParam(std::string alias) {
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(boolParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't an bool parameter...\n"<<std::endl;exit(1);}

	bool retVal=boolParams[name];
	return retVal;
}

std::string ParameterHolder::getStringParam(std::string alias) {
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(stringParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't a string parameter...\n"<<std::endl;exit(1);}

	std::string retVal=stringParams[name];
	return retVal;
}

int ParameterHolder::getParamCount(){
	return allParamNames.size();
}
std::string ParameterHolder::getParamName(int which){
	return allParamNames[which];
}
PHTypes ParameterHolder::getParamType(int which){
	return allParamTypes[which];
}

bool ParameterHolder::supportsParam(std::string alias){
	return (aliases.count(alias)!=0);
}



