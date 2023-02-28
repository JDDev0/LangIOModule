package me.jddev0;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import me.jddev0.module.lang.*;
import me.jddev0.module.lang.LangInterpreter.*;

public class IOModule extends LangNativeModule {
	private final ObjectIDMap<File> openedFiles = new ObjectIDMap<>(file -> file.getAbsolutePath().hashCode());

	@Override
	public DataObject load(List<DataObject> args, final int SCOPE_ID) {
		exportFileFunctions();

		return null;
	}

	private void exportFileFunctions() {
		exportFunctionPointerVariableFinal("openFile", createDataObject(new DataObject.FunctionPointerObject((argumentList, SCOPE_ID) -> {
			
			List<DataObject> combinedArgs = LangUtils.combineArgumentsWithoutArgumentSeparators(argumentList);
			if(combinedArgs.size() != 1)
				return throwError(InterpretingError.INVALID_ARG_COUNT, "1 argument expected", SCOPE_ID);

			DataObject pathObject = combinedArgs.get(0);
			if(pathObject.getType() != DataObject.DataType.TEXT)
				return throwError(InterpretingError.INVALID_ARGUMENTS, "Argument must be of type " + DataObject.DataType.TEXT, SCOPE_ID);

			String path = pathObject.getText();

			if(!new File(path).isAbsolute())
				path = interpreter.getCurrentCallStackElement().getLangPath() + File.separator + path;

			File file = new File(path);

			final int FILE_ID = openedFiles.add(file);

			return createDataObject(FILE_ID);
		})));
		exportFunctionPointerVariableFinal("closeFile", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			openedFiles.remove(fileID);

			return null;
		}));

		exportFunctionPointerVariableFinal("existsFile", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.exists());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("isDirectory", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.isDirectory());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("isReadable", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.canRead());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("isWritable", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.canWrite());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("isExecutable", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.canExecute());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("getModificationDate", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.lastModified());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("setModificationDate", createFileFunctionPointer2Arg((fileID, timeObject, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);

			Number timeNumber = timeObject.toNumber();
			if(timeNumber == null)
				return throwError(InterpretingError.INVALID_ARGUMENTS, "Argument 2 must be a number", SCOPE_ID);

			long time = timeNumber.longValue();
			try {
				return createDataObject(file.setLastModified(time));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("createFile", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.createNewFile());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("makeDirectory", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.mkdir());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("makeDirectories", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.mkdirs());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("rename", createFileFunctionPointer2Arg((fileFromID, fileToIDObject, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileFromID, SCOPE_ID)) != null)
				return errorObject;

			File fileFrom = openedFiles.get(fileFromID);

			Number fileIDNumber = fileToIDObject.toNumber();
			if(fileIDNumber == null)
				return throwError(InterpretingError.INVALID_ARGUMENTS, "Argument 2 must be a number", SCOPE_ID);

			int fileToID = fileIDNumber.intValue();

			if((errorObject = checkFileOpened(fileToID, SCOPE_ID)) != null)
				return errorObject;

			File fileTo = openedFiles.get(fileToID);

			try {
				return createDataObject(fileFrom.renameTo(fileTo));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("delete", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.delete());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("getAbsolutePath", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.getAbsolutePath());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("getCanonicalPath", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.getCanonicalPath());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("readFile", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

				return createDataObject(reader.lines().collect(Collectors.joining("\n")));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("readLines", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

				return createDataObject(reader.lines().map(this::createDataObject).toArray(DataObject[]::new));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("readBytes", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try(InputStream inputStream = new FileInputStream(file)) {
				int len = (int)file.length();
				byte[] bytes = new byte[len];
				int byteCount = inputStream.read(bytes);

				if(byteCount == -1)
					return createDataObject(false);

				return createDataObject(Arrays.copyOf(bytes, byteCount));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("writeFile", createFileFunctionPointer2Arg((fileID, arg, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);

			String data = arg.toText();
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(data);
				writer.flush();

				return null;
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("writeLines", createFileFunctionPointerVarArg((fileID, varArgs, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
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
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("writeBytes", createFileFunctionPointer2Arg((fileID, arg, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			if(arg.getType() != DataObject.DataType.BYTE_BUFFER)
				return throwError(InterpretingError.INVALID_ARGUMENTS, "Argument 2 must be of type " + DataObject.DataType.BYTE_BUFFER, SCOPE_ID);

			File file = openedFiles.get(fileID);

			byte[] byteBuf = arg.getByteBuffer();
			try(OutputStream outputStream = new FileOutputStream(file)) {
				outputStream.write(byteBuf);
				outputStream.flush();

				return null;
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("getSize", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.length());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("getParent", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				return createDataObject(file.getParent());
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("listFilesAndDirectories", createFileFunctionPointer1Arg((fileID, SCOPE_ID) -> {
			DataObject errorObject;
			if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
				return errorObject;

			File file = openedFiles.get(fileID);
			try {
				String[] names = file.list();
				if(names == null)
					return createDataObject();

				return createDataObject(Arrays.stream(names).map(this::createDataObject).toArray(DataObject[]::new));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		}));

		exportFunctionPointerVariableFinal("getFileSystemRoots", createDataObject(new DataObject.FunctionPointerObject((argumentList, SCOPE_ID) -> {
			List<DataObject> combinedArgs = LangUtils.combineArgumentsWithoutArgumentSeparators(argumentList);
			if(combinedArgs.size() != 0)
				return throwError(InterpretingError.INVALID_ARG_COUNT, "0 arguments expected", SCOPE_ID);

			try {
				return createDataObject(Arrays.stream(File.listRoots()).map(File::getAbsolutePath).map(this::createDataObject).toArray(DataObject[]::new));
			}catch(Exception e) {
				return throwError(e, SCOPE_ID);
			}
		})));
	}
	
	@Override
	public DataObject unload(List<DataObject> args, final int SCOPE_ID) {
		if(!openedFiles.isEmpty()) {
			try {
				callPredefinedFunction("println", Arrays.asList(
						createDataObject("WARNING: " + openedFiles.size() + " files were not closed!")
				), SCOPE_ID);
				callPredefinedFunction("println", Arrays.asList(
						createDataObject("The files which were not be closed will be returned as an array of arrays in the format [[fileID1, filePath1], [fileID2, filePath2], ...].")
				), SCOPE_ID);

				DataObject[] notYetClosedFiles = openedFiles.entries().stream().map(entry -> {
					int id = entry.getKey();
					File file = entry.getValue();

					DataObject pathObject;
					try {
						pathObject = createDataObject(file.getAbsolutePath());
					}catch(Exception e) {
						pathObject = throwError(e, SCOPE_ID);
					}

					return createDataObject(new DataObject[] {
							createDataObject(id),
							pathObject
					});
				}).toArray(DataObject[]::new);

				return createDataObject(notYetClosedFiles);
			}finally {
				//Unload all files
				openedFiles.clear();
			}
		}

		return null;
	}

	private DataObject createFileFunctionPointer1Arg(FileFunctionPointer1Arg func) {
		return createDataObject(new DataObject.FunctionPointerObject(func));
	}
	private DataObject createFileFunctionPointer2Arg(FileFunctionPointer2Arg func) {
		return createDataObject(new DataObject.FunctionPointerObject(func));
	}
	private DataObject createFileFunctionPointerVarArg(FileFunctionPointerVarArg func) {
		return createDataObject(new DataObject.FunctionPointerObject(func));
	}

	private DataObject checkFileOpened(int fileID, final int SCOPE_ID) {
		if(!openedFiles.containsId(fileID))
			return throwError(InterpretingError.FILE_NOT_FOUND, "The file with the ID " + fileID + " was not opened", SCOPE_ID);

		return null;
	}

	@FunctionalInterface
	private interface FileFunctionPointer1Arg extends LangExternalFunctionObject {
		DataObject callFileFunc(int fileID, int SCOPE_ID);

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

			return callFileFunc(fileID, SCOPE_ID);
		}
	}

	@FunctionalInterface
	private interface FileFunctionPointer2Arg extends LangExternalFunctionObject {
		DataObject callFileFunc(int fileID, DataObject arg, int SCOPE_ID);

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
			return callFileFunc(fileID, arg, SCOPE_ID);
		}
	}

	@FunctionalInterface
	private interface FileFunctionPointerVarArg extends LangExternalFunctionObject {
		DataObject callFileFunc(int fileID, DataObject[] varArgs, int SCOPE_ID);

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

			return callFileFunc(fileID, combinedArgs.stream().skip(1).map(DataObject::new).toArray(DataObject[]::new), SCOPE_ID);
		}
	}
}