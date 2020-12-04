package com.twi.base.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

public class ExcelRead {

	
	public int totalRows; //sheet中总行数  
    public static int totalCells; //每一行总单元格数  
    /** 
     * read the Excel .xlsx,.xls 
     * @param file jsp中的上传文件 
     * @return 
     * @throws IOException  
     */  
    public List<ArrayList<String>> readExcel(MultipartFile file) throws IOException {  
        try {
        	if(file==null||ExcelUtils.EMPTY.equals(file.getOriginalFilename().trim())){  
                return null;  
            }else{  
                String postfix = ExcelUtils.getPostfix(file.getOriginalFilename());  
                if(!ExcelUtils.EMPTY.equals(postfix)){  
                    if(ExcelUtils.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){  
                        return readXls(file);  
                    }else if(ExcelUtils.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){  
                        return readXlsx(file);  
                    }else{                    
                        return null;  
                    }  
                }  
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;  
    }  
    /** 
     * read the Excel 2010 .xlsx 
     * @param file 
     * @param beanclazz 
     * @param titleExist 
     * @return 
     * @throws IOException  
     */  
    @SuppressWarnings("deprecation")  
    public List<ArrayList<String>> readXlsx(MultipartFile file){  
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();  
        // IO流读取文件  
        Workbook wb = null;  
        ArrayList<String> rowList = null; 
        String targetDirectory = PropertiesUtils.getProperty("files")+"/temp";
        try {  
            // 创建文档  
            wb = WorkbookFactory.create(new File(targetDirectory,file.getOriginalFilename()));                         
            //读取sheet(页)  
            for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){  
                Sheet xssfSheet = wb.getSheetAt(numSheet);  
                if(xssfSheet == null){  
                    continue;  
                }  
                totalRows = xssfSheet.getLastRowNum();
                totalCells = xssfSheet.getRow(0).getLastCellNum();
                //读取Row,从第二行开始  
                for(int rowNum = 1;rowNum <= totalRows;rowNum++){  
                    Row xssfRow = xssfSheet.getRow(rowNum);  
                    if(xssfRow!=null){  
                        rowList = new ArrayList<String>();  
//                        totalCells = xssfRow.getLastCellNum();  
                        //读取列，从第一列开始  
                        for(int c=0;c<=totalCells+1;c++){  
                            Cell cell = xssfRow.getCell(c);  
                            if(cell==null){  
                                rowList.add(ExcelUtils.EMPTY);  
                                continue;  
                            }                             
                            rowList.add(ExcelUtils.getXValue(cell).trim());  
                        }     
                    list.add(rowList);                                            
                    }  
                }  
            }  
            return list;  
        } catch (IOException e) {             
            e.printStackTrace();  
        } catch (InvalidFormatException e) {
			e.printStackTrace();
		} finally{  
        }  
        return null;  
          
    }  
    /** 
     * read the Excel 2003-2007 .xls 
     * @param file 
     * @param beanclazz 
     * @param titleExist 
     * @return 
     * @throws IOException  
     */  
    public List<ArrayList<String>> readXls(MultipartFile file){   
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();  
        // IO流读取文件  
        Workbook wb = null;  
        ArrayList<String> rowList = null;
        String targetDirectory = PropertiesUtils.getProperty("files")+"/temp";   
        try {  
            // 创建文档  
            wb = WorkbookFactory.create(new File(targetDirectory,file.getOriginalFilename()));                         
            //读取sheet(页)  
            for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){  
            	Sheet hssfSheet = wb.getSheetAt(numSheet);  
                if(hssfSheet == null){  
                    continue;  
                }  
                totalRows = hssfSheet.getLastRowNum();
                totalCells = hssfSheet.getRow(0).getLastCellNum(); 
                //读取Row,从第二行开始  
                for(int rowNum = 1;rowNum <= totalRows;rowNum++){  
                	Row hssfRow = hssfSheet.getRow(rowNum);  
                    if(hssfRow!=null){  
                        rowList = new ArrayList<String>();  
//                        totalCells = hssfRow.getLastCellNum();  
                        //读取列，从第一列开始  
                        for(short c=0;c<totalCells;c++){  
                            Cell cell = hssfRow.getCell(c);  
                            if(cell==null){  
                                rowList.add(ExcelUtils.EMPTY);  
                                continue;  
                            }                             
                            rowList.add(ExcelUtils.getHValue(cell).trim());  
                        }          
                        list.add(rowList);  
                    }                     
                }  
            }  
            return list;  
        } catch (IOException e) {             
            e.printStackTrace();  
        } catch (InvalidFormatException e) {
			e.printStackTrace();
		} finally{  
        }  
        return null;  
    }  

}
