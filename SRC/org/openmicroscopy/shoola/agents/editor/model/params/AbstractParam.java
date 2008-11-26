 /*
 * org.openmicroscopy.shoola.agents.editor.model.params.AbstractParam 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.editor.model.params;

//Java imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//Third-party libraries

//Application-internal dependencies


/** 
 * An Abstract superclass of the Parameter object used to model an 
 * experimental variable (or parameter) within a Field (or tree node). 
 * All Parameter objects store their data in an attribute Map<String, String>.
 * 
 * Subclasses must implement the getValueAttributes() method to provide an
 * Array of the attribute names that they use to store the "value" of the
 * parameter (as opposed to name etc).
 * Subclasses may want to implement getDefaultAttributes(), if they
 * have attributes for storing default values.
 * Subclasses must also implement isParamFilled(), depending on how many
 * attributes must be not null in order that the field is 'valid'
 *
 * @author  William Moore &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:will@lifesci.dundee.ac.uk">will@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public abstract class AbstractParam 
	implements IParam 
{
	
	/**
	 * A property of a Parameter object that indicates what type of data
	 * the parameter is. Eg. {link# SingleParam.TEXT_LINE_PARAM}
	 */
	public static final String 		PARAM_TYPE = "paramType";
	
	/**
	 * A property to give a name to this Parameter object. 
	 * Users will set this and it can be displayed in UI. 
	 * eg "Temperature"
	 */
	public static final String 		PARAM_NAME = "paramName";
	
	/**
	 * A map of the attributes that define this parameter. 
	 * Can be used to store any name, value pair. 
	 * E.g. Parameter-Name, Default-Value, etc. 
	 */
	private HashMap<String, String> valueAttributesMap;

	/**
	 * A list of the values for this parameter. Used to store several 
	 * instances of a field, when each parameter in the field will contain
	 * several values. 
	 */
	private List<Object>			paramValues;
	
	/**
	 * Creates an instance, storing the field type in the attributes map.
	 * @see FieldParamsFactory#PARAM_TYPES
	 * 
	 * @param fieldType		A String to define the type of parameter.
	 */
	public AbstractParam(String fieldType) 
	{
		valueAttributesMap = new HashMap<String, String>();
		valueAttributesMap.put(PARAM_TYPE, fieldType);
		
		paramValues = new ArrayList<Object>();
	}
	
	/**
	 * Convenience method for getting all the attributes of this class,
	 * eg for duplicating an instance.
	 * 	
	 * @return		A map of all the attributes for this Parameter
	 */
	public HashMap<String, String> getAllAttributes() 
	{
		return valueAttributesMap;
	}
	
	/**
	 * Convenience method for setting all the attributes of this class,
	 * eg for cloning an instance.
	 * 	
	 * @newAttributes		A map of all the new attributes for this Parameter
	 */
	public void setAllAttributes(HashMap<String, String> newAttributes) 
	{
		valueAttributesMap = newAttributes;
	}
	
	/**
	 * Implemented as specified by the {@link IParam} interface. 
	 * This method should return a list of the names of attributes. 
	 * 
	 * @see IParam#getParamAttributes()
	 */
	public abstract String[] getParamAttributes();

	/**
	 * Unless specified by subclasses, parameter has no default values.
	 * If a list of default values is given, these should be given in the 
	 * same order as the value attributes to which they apply 
	 * 
	 * @see getValueAttributes();
	 */
	public String[] getDefaultAttributes() 
	{
		return new String[] {};
	}
	
	/**
	 * Should return true if the parameter is filled. 
	 */
	public abstract boolean isParamFilled();
	
	/**
	 * Returns a string to identify the type of field. 
	 * @return
	 */
	public String getFieldType() 
	{
		return getAttribute(PARAM_TYPE);
	}
	
	/**
	 * @see		IAttributes.getAttribute(String name)
	 */
	public String getAttribute(String name) 
	{
		return valueAttributesMap.get(name);
	}

	/**
	 * @see		IAttributes.isAttributeTrue(String attributeName)
	 */
	public boolean isAttributeTrue(String attributeName) 
	{
		return (Boolean.valueOf(getAttribute(attributeName)));
	}

	/**
	 * @see		IAttributes.setAttribute(String name, String value)
	 */
	public void setAttribute(String name, String value) 
	{
		//System.out.println("AbstractParam setAttribute() " + 
		//	name + " = " + value);
		valueAttributesMap.put(name, value);
	}
	
	/**
	 * Implemented as specified by {@link IParam#loadDefaultValues()}
	 * 
	 * @see IParam#loadDefaultValues()
	 */
	public HashMap<String, String> loadDefaultValues() 
	{	
		return new HashMap<String, String>();
	}
	
	/**
	 * Implemented as specified by the {@link IParam} interface. 
	 * 
	 * @see IParam#getValueCount()
	 */
	public int getValueCount() 
	{
		return paramValues.size();
	}
	
	/**
	 * Implemented as specified by the {@link IParam} interface. 
	 * 
	 * @see IParam#getValueAt(int)
	 */
	public Object getValueAt(int index) 
	{
		if (index < 0 || index > paramValues.size())
			return null;
		
		return paramValues.get(index);
	}
	
	/**
	 * Implemented as specified by the {@link IParam} interface. 
	 * 
	 * @see IParam#setValueAt(int, Object)
	 */
	public void setValueAt(int index, Object value) 
	{
		if (index < 0 ) return;
		
		// make sure the list is long enough
		while (index+1 > paramValues.size()) {
			paramValues.add("");
		}
		
		paramValues.set(index, value);
	}

	/**
	 * Implemented as specified by the {@link IParam} interface. 
	 * 
	 * @see IParam#insertValue(int, Object)
	 */
	public void insertValue(int index, Object value)
	{
		if (index < 0 ) return;
		
		// make sure the list is long enough
		while (index > paramValues.size()) {
			paramValues.add("");
		}
		paramValues.add(index, value);
	}
	
	/**
	 * Implemented as specified by the {@link IParam} interface. 
	 * 
	 * @see IParam#removeValueAt(int)
	 */
	public void removeValueAt(int index) {
		
		if (index < 0) return;
		if (index > paramValues.size() +1 ) return;
		
		paramValues.remove(index);
	}
	
	/**
	 * Override this to give the Name of the parameter, or it's Type. 
	 */
	public String toString() 
	{
		String name = getAttribute(PARAM_NAME);
		
		if (name == null) {
			name = getAttribute(PARAM_TYPE);
			name = FieldParamsFactory.getTypeForDisplay(name);
		}
		
		return (name == null ? "" : name);
	}
	
	/**
	 * Implemented as specified by the {@link IParam} interface.
	 * Returns a clone of this parameter. 
	 * 
	 * @see IParam#cloneParam()
	 */
	public IParam cloneParam() 
	{
		String paramType = getAttribute(AbstractParam.PARAM_TYPE);
		IParam newParam = FieldParamsFactory.getFieldParam(paramType);
		
		// duplicate the attributes. 
		String name;
		String value;
		Iterator<String> iterator = valueAttributesMap.keySet().iterator();
		while(iterator.hasNext()) {
			name = iterator.next();
			value = getAttribute(name);
			if (value != null)
			newParam.setAttribute(name, value);
		}
		
		// duplicate parameter values
		int index = 0;
		for (Object object : paramValues) 
		{
			newParam.setValueAt(index++, object);
		}
		
		return newParam;
	}
}
