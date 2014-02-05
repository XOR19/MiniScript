package miniscript;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class MiniScriptMessages {

	private static String MESSAGE_FILE = "miniscript_messages";//$NON-NLS-1$
	private static HashMap<String, String> defaultMessages = new HashMap<String, String>();
	private static HashMap<Locale, HashMap<String, String>> messages = new HashMap<Locale, HashMap<String, String>>();
	
	static{
		defaultMessages.put("errors.occured", "Errors occured");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expected.label.end", "Expect : at end of label");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("fatal.error", "Fatal Error");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.one.param", "Expect 1 param for %s but got %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.two.param", "Expect 2 param for %s but got %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.at.least.one.param", "Expect at least 1 param for %s but got %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("no.value.empty", "Expect value for %s but got nothing");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.ptr.start", "Expect '[' for ptr start at %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.ptr.end", "Expect ']' for ptr end at %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.number.at.end", "Expect offset at the end of ptr at %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.register", "Expect 'r' as start of registers at %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("expect.number.for.register", "Expect number for 0 to 31 for register index at %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("register.out.of.range", "Expect number for 0 to 31 for register index at %s but got %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("wrong.number.format", "Wrong nuber format at %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("const.not.accepted", "Const values not accepted at start for %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("function.not.exists", "Function %s not exists");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("unknow.instruction", "Unknown instruction %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("assertion.error", "This should never be happend :/");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("ram.not.intarray.or.null", "RAM isn't a int array or is null");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("registers.not.intarray.or.null", "Register array isn't a int array or is null");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("not.32.registers", "Expect an array size or 32 for registers");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("allready.exectuting", "Script is allready executing, please wait");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("empty.label", "Empty label");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("duplicated.label", "Duplicated label %s, bevore declared here: %s");//$NON-NLS-1$ //$NON-NLS-2$
		defaultMessages.put("label.with.bad.characters", "Bad characters in label %s");//$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private static HashMap<String, String> loadLocale(Locale locale){
		try{
			ResourceBundle bundle = ResourceBundle.getBundle(MESSAGE_FILE, locale);
			HashMap<String, String> messages = new HashMap<String, String>();
			for(String key:bundle.keySet()){
				try {
					messages.put(key, bundle.getString(key));
				} catch (MissingResourceException e) {}
			}
			return messages;
		}catch(MissingResourceException e){
			if(locale==Locale.US)
				return defaultMessages;
			return null;
		}
	}
	
	private static HashMap<String, String> getLocale(Locale locale){
		HashMap<String, String> keys = messages.get(locale);
		if(keys==null){
			keys = loadLocale(locale);
			if(keys==null){
				if(locale==Locale.US){
					keys = new HashMap<String, String>();
				}else{
					keys = getLocale(Locale.US);
				}
			}
		}
		return keys;
	}
	
	private static String getLocateMessageOrEnglish(String message, Locale locale){
		HashMap<String, String> keys = getLocale(locale);
		String locMessage = keys.get(message);
		if(locMessage==null){
			keys = getLocale(Locale.US);
			locMessage = keys.get(message);
		}
		return locMessage;
	}
	
	static String getLocaleMessage(String message, Locale locale, Object...args){
		String locMessage = getLocateMessageOrEnglish(message, locale);
		if(locMessage==null){
			return "!"+message+"!";//$NON-NLS-1$ //$NON-NLS-2$
		}
		return String.format(locMessage, args);
	}
	
	static String getLocaleMessage(String message, Object...args){
		return getLocaleMessage(message, Locale.getDefault(), args);
	}
	
	private MiniScriptMessages(){
		throw new InstantiationError();
	}
	
}
