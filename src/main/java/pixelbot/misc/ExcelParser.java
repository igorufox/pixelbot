package pixelbot.misc;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelParser {

	/** POI 4 dropped Cell.setCellType(STRING) as a way to stringify numbers and formulas. */
	private static final DataFormatter FORMATTER = new DataFormatter();

	public static Map<String, List<Map<String, String>>> parseXLS(String file_name) {
		Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();

		List<Map<String, String>> r_sheet = null;
		Map<String, String> r_row = null;

		try (InputStream is = new FileInputStream(file_name)) {

			// POIFSFileSystem pfs = new POIFSFileSystem(is);
			Workbook wb = new XSSFWorkbook(is);

			// Iterate over all XLS file sheets
			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				Sheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();
				if (rows > 1) {
					r_sheet = new ArrayList<Map<String, String>>();

					Row row = null;
					Row header_row = null;
					Cell cell = null;
					String value = null;

					header_row = sheet.getRow(0);

					// Iterate over all rows
					for (int r = 1; r < rows; r++) {
						row = sheet.getRow(r);

						if (row != null) {
							r_row = new HashMap<String, String>();
							int cells = row.getLastCellNum();

							// Iterate over all cells
							for (int c = 0; c < cells; c++) {

								cell = row.getCell(c);
								value = "";
								if (cell != null) {

									switch (cell.getCellType()) {
									case FORMULA:
									case NUMERIC:
										value = FORMATTER.formatCellValue(cell);
										break;
									case STRING:
										value = cell.getStringCellValue();
										break;

									case BLANK:
									default:
										value = "";
									}

								}
								r_row.put(header_row.getCell(c).getStringCellValue(), value);

							}
							r_sheet.add(r_row);
						}

					}
					result.put(sheet.getSheetName(), r_sheet);
				}
			}
		} catch (Exception ex) {}
		return result;
	}

}
