package me.jddev0;

import java.io.File;
import java.io.IOException;
import java.util.*;

import me.jddev0.module.lang.*;
import me.jddev0.module.lang.LangInterpreter.*;

public class IOModule extends LangNativeModule {
	private final Map<Integer, File> openedFiles = new HashMap<>();
	private int lastFileID;

	public IOModule() {
		//ID should not start at 0 (User will not be able to use hard-coded file ids)
		lastFileID = hashCode();
	}

	@Override
	public DataObject load(List<DataObject> args, final int SCOPE_ID) {
		exportFunctionPointerVariableFinal("openFile", createDataObject(new DataObject.FunctionPointerObject((argumentList, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			List<DataObject> combinedArgs = LangUtils.combineArgumentsWithoutArgumentSeparators(argumentList);
			if(combinedArgs.size() != 1)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARG_COUNT, "1 argument expected", INNER_SCOPE_ID);

			DataObject pathObject = combinedArgs.get(0);
			if(pathObject.getType() != DataObject.DataType.TEXT)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARGUMENTS, "Argument must be of type " + DataObject.DataType.TEXT, INNER_SCOPE_ID);

			final int FILE_ID = lastFileID++;
			File file = new File(pathObject.getText());

			openedFiles.put(FILE_ID, file);

			return createDataObject(FILE_ID);
		})));
		exportFunctionPointerVariableFinal("closeFile", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			openedFiles.remove(fileID);

			return null;
		})));

		exportFunctionPointerVariableFinal("existsFile", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			return createDataObject(file.exists());
		})));
		exportFunctionPointerVariableFinal("isDirectory", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			return createDataObject(file.isDirectory());
		})));

		exportFunctionPointerVariableFinal("isReadable", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			return createDataObject(file.canRead());
		})));
		exportFunctionPointerVariableFinal("isWritable", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			return createDataObject(file.canWrite());
		})));
		exportFunctionPointerVariableFinal("isExecutable", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			return createDataObject(file.canExecute());
		})));

		exportFunctionPointerVariableFinal("createFile", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.createNewFile());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("makeDirectory", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.mkdir());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("makeDirectories", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.mkdirs());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));

		return null;
	}
	
	@Override
	public DataObject unload(List<DataObject> args, final int SCOPE_ID) {
		//Unload all files
		openedFiles.clear();

		return null;
	}

	private DataObject checkFileOpened(LangInterpreterInterface lii, int fileID, final int SCOPE_ID) {
		if(!openedFiles.containsKey(fileID))
			return lii.setErrnoErrorObject(InterpretingError.FILE_NOT_FOUND, "The file with the ID " + fileID + " was not opened", SCOPE_ID);

		return null;
	}

	@FunctionalInterface
	private interface FileFunctionPointer1Arg extends LangExternalFunctionObject {
		DataObject callFileFunc(LangInterpreter interpreter, int fileID, int SCOPE_ID);

		@Override
		default DataObject callFunc(LangInterpreter interpreter, List<DataObject> argumentList, int SCOPE_ID) {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			List<DataObject> combinedArgs = LangUtils.combineArgumentsWithoutArgumentSeparators(argumentList);
			if(combinedArgs.size() != 1)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARG_COUNT, "1 argument expected", SCOPE_ID);

			DataObject fileIDObject = combinedArgs.get(0);
			Number fileIDNumber = fileIDObject.toNumber();
			if(fileIDNumber == null)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARGUMENTS, "Argument must be a number", SCOPE_ID);

			int fileID = fileIDNumber.intValue();

			return callFileFunc(interpreter, fileID, SCOPE_ID);
		}
	}
}