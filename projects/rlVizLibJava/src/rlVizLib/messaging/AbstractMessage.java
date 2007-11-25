/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package rlVizLib.messaging;



public class AbstractMessage {
	private GenericMessage theRealMessageObject=null;
	
	public AbstractMessage(GenericMessage theMessageObject){
		this.theRealMessageObject=theMessageObject;
	}

        public String getToName(){
            return MessageUser.name(getTo());
        }
        public String getFromName(){
            return MessageUser.name(getFrom());
        }
        public String getPayLoadTypeName(){
            return MessageValueType.name(getPayLoadType());
        }
	/**
	 * @return the theMessageType
	 */
	public int getTheMessageType() {
		return theRealMessageObject.getTheMessageType();
	}

	/**
	 * @return the from
	 */
	public MessageUser getFrom() {
		return theRealMessageObject.getFrom();
	}

	/**
	 * @return the to
	 */
	public MessageUser getTo() {
		return theRealMessageObject.getTo();
	}

	/**
	 * @return the payLoadType
	 */
	public MessageValueType getPayLoadType() {
		return theRealMessageObject.getPayLoadType();
	}

	/**
	 * @return the payLoad
	 */
	public String getPayLoad() {
		return theRealMessageObject.getPayLoad();
	}
	
	/*
	 * Override this if you can handle automatically given a queryable environment or agent
	 */
	public boolean canHandleAutomatically(Object theReceiver) {
		return false;
	}
	

	public static String makeMessage(int TO, int FROM, int CMD, int VALTYPE,String PAYLOAD) {
		StringBuffer theRequestBuffer=new StringBuffer();
		theRequestBuffer.append("TO=");
		theRequestBuffer.append(TO);
		theRequestBuffer.append(" FROM=");
		theRequestBuffer.append(FROM);
		theRequestBuffer.append(" CMD=");
		theRequestBuffer.append(CMD);
		theRequestBuffer.append(" VALTYPE=");
		theRequestBuffer.append(VALTYPE);
		theRequestBuffer.append(" VALS=");
		theRequestBuffer.append(PAYLOAD);
		
		return theRequestBuffer.toString();
	}


}
