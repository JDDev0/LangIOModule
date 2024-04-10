package me.jddev0.lang.modules.io;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import me.jddev0.lang.modules.io.util.ObjectIDMap;
import at.jddev0.lang.*;
import at.jddev0.lang.LangInterpreter.*;

import static at.jddev0.lang.LangFunction.*;
import static at.jddev0.lang.LangFunction.LangParameter.*;

public class IOModule extends LangNativeModule {
	private final ObjectIDMap<File> openedFiles = new ObjectIDMap<>(file -> file.getAbsolutePath().hashCode());

	@Override
	public DataObject load(List<DataObject> args, final int SCOPE_ID) {
		exportFileFunctions();

		return null;
	}

	private void exportFileFunctions() {
		exportFunctionPointerVariableFinal("openFile", createNativeFunctionDataObject(new Object() {
			@LangFunction("openFile")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject openFileFunction(
					int SCOPE_ID,
					@LangParameter("$path") @VarArgs DataObject pathObject
			) {
				String path = pathObject.getText();

				if (!new File(path).isAbsolute())
					path = interpreter.getCurrentCallStackElement().getLangPath() + File.separator + path;

				File file = new File(path);

				final int FILE_ID = openedFiles.add(file);

				return createDataObject(FILE_ID);
			}
		}));
		exportFunctionPointerVariableFinal("closeFile", createNativeFunctionDataObject(new Object() {
			@LangFunction("closeFile")
			@AllowedTypes(DataObject.DataType.VOID)
			public DataObject closeFileFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				openedFiles.remove(fileID);

				return null;
			}
		}));

		exportFunctionPointerVariableFinal("existsFile", createNativeFunctionDataObject(new Object() {
			@LangFunction("existsFile")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject existsFileFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.exists());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("isDirectory", createNativeFunctionDataObject(new Object() {
			@LangFunction("isDirectory")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject isDirectoryFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.isDirectory());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("isReadable", createNativeFunctionDataObject(new Object() {
			@LangFunction("isReadable")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject isReadableFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.canRead());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("isWritable", createNativeFunctionDataObject(new Object() {
			@LangFunction("isWritable")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject isWritableFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.canWrite());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("isExecutable", createNativeFunctionDataObject(new Object() {
			@LangFunction("isExecutable")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject isExecutableFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.canExecute());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("getModificationDate", createNativeFunctionDataObject(new Object() {
			@LangFunction("getModificationDate")
			@AllowedTypes(DataObject.DataType.LONG)
			public DataObject getModificationDateFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.lastModified());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("setModificationDate", createNativeFunctionDataObject(new Object() {
			@LangFunction("setModificationDate")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject setModificationDateFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber,
					@LangParameter("$time") @NumberValue Number timeNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);

				long time = timeNumber.longValue();
				try {
					return createDataObject(file.setLastModified(time));
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("createFile", createNativeFunctionDataObject(new Object() {
			@LangFunction("createFile")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject createFileFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.createNewFile());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("makeDirectory", createNativeFunctionDataObject(new Object() {
			@LangFunction("makeDirectory")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject makeDirectoryFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.mkdir());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("makeDirectories", createNativeFunctionDataObject(new Object() {
			@LangFunction("makeDirectories")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject makeDirectoriesFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.mkdirs());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("rename", createNativeFunctionDataObject(new Object() {
			@LangFunction("rename")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject renameFunction(
					int SCOPE_ID,
					@LangParameter("$fileFromID") @NumberValue Number fileFromIDNumber,
					@LangParameter("$fileToID") @NumberValue Number fileToIDNumber
			) {
				int fileFromID = fileFromIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileFromID, SCOPE_ID)) != null)
					return errorObject;

				File fileFrom = openedFiles.get(fileFromID);

				int fileToID = fileToIDNumber.intValue();

				if((errorObject = checkFileOpened(fileToID, SCOPE_ID)) != null)
					return errorObject;

				File fileTo = openedFiles.get(fileToID);

				try {
					return createDataObject(fileFrom.renameTo(fileTo));
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("delete", createNativeFunctionDataObject(new Object() {
			@LangFunction("delete")
			@AllowedTypes(DataObject.DataType.INT)
			public DataObject deleteFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.delete());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("getAbsolutePath", createNativeFunctionDataObject(new Object() {
			@LangFunction("getAbsolutePath")
			@AllowedTypes(DataObject.DataType.TEXT)
			public DataObject getAbsolutePathFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.getAbsolutePath());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("getCanonicalPath", createNativeFunctionDataObject(new Object() {
			@LangFunction("getCanonicalPath")
			@AllowedTypes(DataObject.DataType.TEXT)
			public DataObject getCanonicalPathFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.getCanonicalPath());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("readFile", createNativeFunctionDataObject(new Object() {
			@LangFunction("readFile")
			@AllowedTypes(DataObject.DataType.TEXT)
			public DataObject readFileFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
					return createDataObject(reader.lines().collect(Collectors.joining("\n")));
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("readLines", createNativeFunctionDataObject(new Object() {
			@LangFunction("readLines")
			@AllowedTypes(DataObject.DataType.ARRAY)
			public DataObject readLinesFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
					return createDataObject(reader.lines().map(IOModule.this::createDataObject).toArray(DataObject[]::new));
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("readBytes", createNativeFunctionDataObject(new Object() {
			@LangFunction("readBytes")
			@AllowedTypes(DataObject.DataType.BYTE_BUFFER)
			public DataObject readBytesFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

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
			}
		}));

		exportFunctionPointerVariableFinal("writeFile", createNativeFunctionDataObject(new Object() {
			@LangFunction("writeFile")
			@AllowedTypes(DataObject.DataType.VOID)
			public DataObject writeFileFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber,
					@LangParameter("$data") @VarArgs DataObject dataObject
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);

				String data = dataObject.toText();
				try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
					writer.write(data);
					writer.flush();

					return null;
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("writeLines", createNativeFunctionDataObject(new Object() {
			@LangFunction("writeLines")
			@AllowedTypes(DataObject.DataType.VOID)
			public DataObject writeLinesFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber,
					@LangParameter("&args") @VarArgs List<DataObject> args
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
					for(DataObject arg:args) {
						writer.write(arg.toText());
						writer.newLine();
					}
					writer.flush();

					return null;
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("writeBytes", createNativeFunctionDataObject(new Object() {
			@LangFunction("writeBytes")
			@AllowedTypes(DataObject.DataType.VOID)
			public DataObject writeBytesFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber,
					@LangParameter("$data") @AllowedTypes(DataObject.DataType.BYTE_BUFFER) DataObject dataObject
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);

				byte[] byteBuf = dataObject.getByteBuffer();
				try(OutputStream outputStream = new FileOutputStream(file)) {
					outputStream.write(byteBuf);
					outputStream.flush();

					return null;
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("getSize", createNativeFunctionDataObject(new Object() {
			@LangFunction("getSize")
			@AllowedTypes(DataObject.DataType.LONG)
			public DataObject getSizeFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.length());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("getParent", createNativeFunctionDataObject(new Object() {
			@LangFunction("getParent")
			@AllowedTypes(DataObject.DataType.TEXT)
			public DataObject getParentFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					return createDataObject(file.getParent());
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));

		exportFunctionPointerVariableFinal("listFilesAndDirectories", createNativeFunctionDataObject(new Object() {
			@LangFunction("listFilesAndDirectories")
			@AllowedTypes(DataObject.DataType.ARRAY)
			public DataObject listFilesAndDirectoriesFunction(
					int SCOPE_ID,
					@LangParameter("$fileID") @NumberValue Number fileIDNumber
			) {
				int fileID = fileIDNumber.intValue();

				DataObject errorObject;
				if((errorObject = checkFileOpened(fileID, SCOPE_ID)) != null)
					return errorObject;

				File file = openedFiles.get(fileID);
				try {
					String[] names = file.list();
					if(names == null)
						return createDataObject();

					return createDataObject(Arrays.stream(names).map(IOModule.this::createDataObject).toArray(DataObject[]::new));
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
		exportFunctionPointerVariableFinal("getFileSystemRoots", createNativeFunctionDataObject(new Object() {
			@LangFunction("getFileSystemRoots")
			@AllowedTypes(DataObject.DataType.ARRAY)
			public DataObject getFileSystemRootsFunction(
					int SCOPE_ID
			) {
				try {
					return createDataObject(Arrays.stream(File.listRoots()).map(File::getAbsolutePath).map(IOModule.this::createDataObject).toArray(DataObject[]::new));
				}catch(Exception e) {
					return throwError(e, SCOPE_ID);
				}
			}
		}));
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

	private DataObject createNativeFunctionDataObject(Object obj) {
		return createDataObject(new DataObject.FunctionPointerObject(
				LangNativeFunction.getSingleLangFunctionFromObject(obj)));
	}

	private DataObject checkFileOpened(int fileID, final int SCOPE_ID) {
		if(!openedFiles.containsId(fileID))
			return throwError(InterpretingError.FILE_NOT_FOUND, "The file with the ID " + fileID + " was not opened", SCOPE_ID);

		return null;
	}
}