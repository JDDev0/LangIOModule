package me.jddev0;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import me.jddev0.module.lang.*;
import me.jddev0.module.lang.LangInterpreter.*;

public class IOModule extends LangNativeModule {
	private final Map<Integer, File> openedFiles = new HashMap<>();
	private int currentFileID;

	public IOModule() {
		currentFileID = 0;
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

			File file = new File(pathObject.getText());
			final int FILE_ID = generateNextFileID(file);

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
			try {
				return createDataObject(file.exists());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("isDirectory", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.isDirectory());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));

		exportFunctionPointerVariableFinal("isReadable", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.canRead());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("isWritable", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.canWrite());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("isExecutable", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.canExecute());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
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

		exportFunctionPointerVariableFinal("delete", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.delete());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));

		exportFunctionPointerVariableFinal("getAbsolutePath", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.getAbsolutePath());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("getCanonicalPath", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.getCanonicalPath());
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));

		exportFunctionPointerVariableFinal("readFile", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

				return createDataObject(reader.lines().collect(Collectors.joining("\n")));
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("readLines", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer1Arg)(interpreter, fileID, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

				return createDataObject(reader.lines().map(this::createDataObject).toArray(DataObject[]::new));
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));

		exportFunctionPointerVariableFinal("writeFile", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointer2Arg)(interpreter, fileID, arg, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);

			String data = arg.toText();
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(data);
				writer.flush();

				return null;
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));
		exportFunctionPointerVariableFinal("writeLines", createDataObject(new DataObject.FunctionPointerObject((FileFunctionPointerVarArg)(interpreter, fileID, varArgs, INNER_SCOPE_ID) -> {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			DataObject errorObject;
			if((errorObject = checkFileOpened(lii, fileID, INNER_SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);

			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for(DataObject arg:varArgs) {
					writer.write(arg.toText());
					writer.newLine();
				}
				writer.flush();

				return null;
			}catch(Exception e) {
				return lii.setErrnoErrorObject(InterpretingError.SYSTEM_ERROR, e.getClass().getSimpleName() + " " + e.getMessage(), INNER_SCOPE_ID);
			}
		})));

		return null;
	}
	
	@Override
	public DataObject unload(List<DataObject> args, final int SCOPE_ID) {
		if(!openedFiles.isEmpty()) {
			try {
				callPredefinedFunction("println", Arrays.asList(
						createDataObject("WARNING: " + openedFiles.size() + " files were not closed!")
				), SCOPE_ID);
				callPredefinedFunction("println", Arrays.asList(
						createDataObject("The IDs of the files which were not be closed will be returned as an array.")
				), SCOPE_ID);

				DataObject[] notYetClosedFiles = openedFiles.keySet().stream().map(this::createDataObject).toArray(DataObject[]::new);

				return createDataObject(notYetClosedFiles);
			}finally {
				//Unload all files
				openedFiles.clear();
			}
		}

		return null;
	}

	/**
	 * Should prevent devs from introducing bugs by guessing or calculating file ids [THIS IS NO SECURITY FEATURE]
	 */
	private int generateNextFileID(File file) {
		do {
			int selfHashCode = hashCode();
			do {
				selfHashCode += (int)System.currentTimeMillis() + (int)System.nanoTime();
			}while(selfHashCode == 0);

			int pathHashCode = file.getAbsolutePath().hashCode();
			do {
				selfHashCode += (int)System.currentTimeMillis() + (int)System.nanoTime();
			}while(selfHashCode == 0);

			this.currentFileID += pathHashCode % selfHashCode + selfHashCode;
		}while(openedFiles.containsKey(this.currentFileID));

		return currentFileID;
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

	@FunctionalInterface
	private interface FileFunctionPointer2Arg extends LangExternalFunctionObject {
		DataObject callFileFunc(LangInterpreter interpreter, int fileID, DataObject arg, int SCOPE_ID);

		@Override
		default DataObject callFunc(LangInterpreter interpreter, List<DataObject> argumentList, int SCOPE_ID) {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			List<DataObject> combinedArgs = LangUtils.combineArgumentsWithoutArgumentSeparators(argumentList);
			if(combinedArgs.size() != 2)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARG_COUNT, "2 arguments expected", SCOPE_ID);

			DataObject fileIDObject = combinedArgs.get(0);
			Number fileIDNumber = fileIDObject.toNumber();
			if(fileIDNumber == null)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARGUMENTS, "Argument 1 must be a number", SCOPE_ID);

			int fileID = fileIDNumber.intValue();

			DataObject arg = combinedArgs.get(1);
			return callFileFunc(interpreter, fileID, arg, SCOPE_ID);
		}
	}

	@FunctionalInterface
	private interface FileFunctionPointerVarArg extends LangExternalFunctionObject {
		DataObject callFileFunc(LangInterpreter interpreter, int fileID, DataObject[] varArgs, int SCOPE_ID);

		@Override
		default DataObject callFunc(LangInterpreter interpreter, List<DataObject> argumentList, int SCOPE_ID) {
			LangInterpreterInterface lii = new LangInterpreterInterface(interpreter);

			List<DataObject> combinedArgs = LangUtils.combineArgumentsWithoutArgumentSeparators(argumentList);
			if(combinedArgs.size() < 1)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARG_COUNT, "At least 1 argument must be provided", SCOPE_ID);

			DataObject fileIDObject = combinedArgs.get(0);
			Number fileIDNumber = fileIDObject.toNumber();
			if(fileIDNumber == null)
				return lii.setErrnoErrorObject(InterpretingError.INVALID_ARGUMENTS, "Argument 1 must be a number", SCOPE_ID);

			int fileID = fileIDNumber.intValue();

			return callFileFunc(interpreter, fileID, combinedArgs.stream().skip(1).map(DataObject::new).toArray(DataObject[]::new), SCOPE_ID);
		}
	}
}