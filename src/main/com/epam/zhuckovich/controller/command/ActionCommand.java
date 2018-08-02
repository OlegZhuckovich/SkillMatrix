package com.epam.zhuckovich.controller.command;

import com.epam.zhuckovich.controller.entity.Category;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

class ActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(ActionCommand.class);

    private static final String CATEGORY_ID = "categoryID";
    private static final String CATEGORY_LIST = "categoryList";
    private static final String FILE_PATH = "/Users/Oleg/IdeaProjects/FinalTask2/src/resources/SkillMatrix.xlsx";
    private static final String INPUT_VALUE = "inputValue";
    private static final String SHEET_NAME = "Skill Matrix";

    private static final String BLUE_COLOR = "#1862db";
    private static final String PINK_COLOR = "#b018db";
    private static final String ORANGE_COLOR = "#db6c18";
    private static final String GREEN_COLOR = "#157a0f";
    private static final String AQUA_COLOR = "#0d8084";
    private static final String PURPLE_COLOR = "#43ab32";

    private static final Character UNDERLINE_SYMBOL = '_';


    private static ArrayList<String> colorArrayList;

    static {
        colorArrayList = new ArrayList<>();
        colorArrayList.add(BLUE_COLOR);
        colorArrayList.add(PINK_COLOR);
        colorArrayList.add(ORANGE_COLOR);
        colorArrayList.add(GREEN_COLOR);
        colorArrayList.add(AQUA_COLOR);
    }

    void readExcelFile(HttpServletRequest request, HttpServletResponse response){
        try {
            Workbook workbook = WorkbookFactory.create(new File(FILE_PATH));
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            ArrayList<Category> categoryList = new ArrayList<>();
            for (Row row: sheet) {
                for(Cell cell: row) {
                    if(!cell.getStringCellValue().isEmpty()) {
                        Category currentCategory = new Category(cell.getStringCellValue());
                        currentCategory.setRowIndex(cell.getAddress().getRow());
                        currentCategory.setColumnIndex(cell.getAddress().getColumn());
                        categoryList.add(currentCategory);
                    }
                }
            }
            workbook.close();
            ArrayList<ArrayList<Category>> sortedList = new ArrayList<>();
            int i = 0;
            while(!categoryList.isEmpty()){
                ArrayList<Category> tempCategoryList = new ArrayList<>();
                for(Category category:categoryList){
                    if(category.getColumnIndex()==i){
                        tempCategoryList.add(category);
                    }
                }
                if(!tempCategoryList.isEmpty()){
                    sortedList.add(tempCategoryList);
                    i++;
                } else {
                    break;
                }
            }
            ArrayList<Category> finalArrayList = new ArrayList<>();
            while(!sortedList.isEmpty()){
                ArrayList<Category> lastList = sortedList.get(sortedList.size()-1);
                ArrayList<Category> penultimateList = sortedList.get(sortedList.size()-2);
                for(Category categoryInLastList:lastList){
                    int counter = 0;
                    for(int temp=0; temp<penultimateList.size()-1; temp++){
                        if(penultimateList.get(temp).getRowIndex() < categoryInLastList.getRowIndex() && categoryInLastList.getRowIndex() < penultimateList.get(temp+1).getRowIndex()){
                            penultimateList.get(temp).addCategory(categoryInLastList);
                            counter++;
                            break;
                        }
                    }
                    if(counter == 0){
                        penultimateList.get(penultimateList.size()-1).addCategory(categoryInLastList);
                    }
                }
                sortedList.remove(lastList);
                if(sortedList.size() == 1){
                    finalArrayList = sortedList.get(0);
                    sortedList.remove(0);
                }
            }
            request.getSession().setAttribute(CATEGORY_LIST, finalArrayList);
            ArrayList<String> finalAjaxResponseList = buildAjaxResponse(finalArrayList);
            StringBuilder finalAjaxResponse = new StringBuilder();
            for (String currentString : finalAjaxResponseList) {
                finalAjaxResponse.append(currentString);
            }
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(finalAjaxResponse.toString());
        } catch (IOException e) {
            LOGGER.log(Level.ERROR,"IOException was occurred");
        } catch (InvalidFormatException e) {
            LOGGER.log(Level.ERROR,"InvalidFormatException was occurred");
        }
    }

    private ArrayList<String> buildAjaxResponse(ArrayList<Category> categoryArrayList){
        ArrayList<String> stringCategoryArrayList = new ArrayList<>();
        if(categoryArrayList!=null){
            stringCategoryArrayList.add("<ul>\n");
            for(Category category:categoryArrayList){
                int color = category.getColumnIndex()%5;
                stringCategoryArrayList.add("<li id='li" + category.getColumnIndex() + "_" + category.getRowIndex() + "' style='background-color:" + colorArrayList.get(color) + "'>\n <a>" + category.getCategoryName() + "</a>\n <input type='text' id='" + category.getColumnIndex() + "--" + category.getRowIndex() + "' value='" + category.getCategoryName() + "'>\n <img src='images/addCategory.svg' width='30px' height='30px' id='" +
                        category.getColumnIndex() + "_" + category.getRowIndex() + "'>\n <img src='images/deleteCategory.svg' width='30px' height='30px' id='" + category.getColumnIndex() + "-" +category.getRowIndex() + "'>\n <img src='images/openCloseCategory.svg' width='30px' height='30px' id='" + category.getColumnIndex() + "__" +category.getRowIndex() + "'>\n");
                String quantityString = "";
                if(category.getSubcategoryList()!=null){
                    quantityString += "    <span style='color:white;font-size: 20px;'>" + category.getSubcategoryQuantity() + "</span>\n";
                } else {
                    quantityString += "    <span style='color:white;font-size: 20px;'>0</span>\n";
                }
                stringCategoryArrayList.add(quantityString);
                String scriptString = "<script charset=\"utf-8\">\n" +
                        "        $('#"+ category.getColumnIndex() + "_" + category.getRowIndex() + "').on('click',function () {\n" +
                        "           var subcategoryName = prompt('Enter the name of the subcategory');\n" +
                        "           if(subcategoryName != null){\n" +
                        "               $.ajax({\n" +
                        "                   url: 'http://localhost:8000/controller',\n" +
                        "                   method: 'post',\n" +
                        "                   data: {\n" +
                        "                       'inputValue' : subcategoryName,\n" +
                        "                       'categoryID' : '" + category.getColumnIndex() + "_" + category.getRowIndex() + "',\n" +
                        "                       'command' : 'add_data'\n" +
                        "                   },\n" +
                        "                   success: function (data) {\n" +
                        "                       var mainList = $('#mainList');\n" +
                        "                       mainList.empty();\n" +
                        "                       mainList.append(data);\n" +
                        "                   }\n" +
                        "               });\n" +
                        "           }\n" +
                        "        });\n";
                String editCategoryString = "$('#"+ category.getColumnIndex() + "--" + category.getRowIndex() + "').focusout(function() {\n" +
                        "      if($(this).val()){\n" +
                        "        $.ajax({\n" +
                        "            url: 'http://localhost:8000/controller',\n" +
                        "            method: \"post\",\n" +
                        "            data: {\n" +
                        "                'inputValue': $('#"+ category.getColumnIndex() + "--" + category.getRowIndex() + "').val(),\n" +
                        "                'categoryID': '" + category.getColumnIndex() + "_" + category.getRowIndex() + "',\n" +
                        "                'command': 'edit_data' \n" +
                        "            },\n" +
                        "            success: function(data) {\n" +
                        "                var mainList = $('#mainList');\n" +
                        "                mainList.empty();\n" +
                        "                mainList.append(data);\n" +
                        "            }\n" +
                        "        });\n" +
                        "      } else {\n" +
                        "           alert('Category should not be empty! The changes will not be reflected in the Excel file');\n" +
                        "      }\n" +
                        "    });";
                String deleteCategoryString = "$('#" + category.getColumnIndex() + "-" +category.getRowIndex() + "').on(\"click\",function () {\n" +
                        "        $.ajax({\n" +
                        "            url: \"http://localhost:8000/controller\",\n" +
                        "            method: \"post\",\n" +
                        "            data: {\n" +
                        "               'categoryID': '" + category.getColumnIndex() + "_" + category.getRowIndex() + "',\n "+
                        "               'command': 'delete_data' " +
                        "            },\n" +
                        "            success: function (data) {\n" +
                        "                     var mainList = $('#mainList');\n" +
                        "                     mainList.empty();\n" +
                        "                     mainList.append(data);\n" +
                        "            }\n" +
                        "        });\n" +
                        "    });\n" +
                        "       $('#" + category.getColumnIndex() + "__" +category.getRowIndex() + "').on('click',function (event) {\n" +
                        "                event.stopPropagation();\n" +
                        "                $('#li" + category.getColumnIndex() + "_" + category.getRowIndex() + "').children('ul').slideToggle();\n" +
                        "       });\n" +
                        "</script>";
                scriptString += editCategoryString;
                scriptString += deleteCategoryString;
                stringCategoryArrayList.add(scriptString);
                ArrayList<String> innerStringCategoryArrayList = buildAjaxResponse(category.getSubcategoryList());
                for(String currentString: innerStringCategoryArrayList){
                    stringCategoryArrayList.add(currentString);
                }
                stringCategoryArrayList.add("</li>\n");
            }
            stringCategoryArrayList.add("</ul>\n");
        }
        return stringCategoryArrayList;
    }

    private void rewriteExcelFile(ArrayList<Category> categoryArrayList, HttpServletRequest request, HttpServletResponse response) {
        int columnCounter = -1;
        int rowCounter = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(FILE_PATH));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            workbook.removeSheetAt(0);
            XSSFSheet worksheet = workbook.createSheet(SHEET_NAME);
            if(categoryArrayList!=null){
                rewriteExcelFile(categoryArrayList, worksheet, columnCounter, rowCounter);
            }
            fileInputStream.close();
            FileOutputStream fileOutputStream =new FileOutputStream(new File(FILE_PATH));
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            readExcelFile(request, response);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.ERROR,"FileNotFoundException was occurred during addSubcategory operation");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR,"IOException was occurred during addSubcategory operation");
        }
    }

    private int rewriteExcelFile(ArrayList<Category> categoryArrayList, XSSFSheet worksheet, int columnCounter, int rowCounter){
        columnCounter++;
        for(Category category: categoryArrayList){
            Row currentRow = worksheet.getRow(rowCounter);
            if(currentRow == null){
                currentRow = worksheet.createRow(rowCounter);
            }
            Cell currentCell = currentRow.getCell(columnCounter);
            if(currentCell == null){
                currentCell = currentRow.createCell(columnCounter);
            }
            currentCell.setCellValue(category.getCategoryName());
            rowCounter++;
            if(category.getSubcategoryList()!=null) {
                rowCounter = rewriteExcelFile(category.getSubcategoryList(), worksheet, columnCounter, rowCounter);
            }
        }
        return rowCounter;
    }

    void changeList(HttpServletRequest request, HttpServletResponse response, CommandType commandType){
        String categoryID = request.getParameter(CATEGORY_ID);
        int column = Integer.parseInt(categoryID.substring(0, categoryID.indexOf(UNDERLINE_SYMBOL)));
        int row = Integer.parseInt(categoryID.substring(categoryID.indexOf(UNDERLINE_SYMBOL)+1, categoryID.length()));
        ArrayList<Category> categoryArrayList = (ArrayList<Category>) request.getSession().getAttribute(CATEGORY_LIST);
        if(categoryArrayList!=null){
            if(commandType == CommandType.ADD_DATA){
                categoryArrayList = addSubcategory(categoryArrayList, new Category(request.getParameter(INPUT_VALUE)), column, row);
            } else if (commandType == CommandType.DELETE_DATA){
                categoryArrayList = deleteCategory(categoryArrayList,column, row);
            } else {
                categoryArrayList = editCategory(categoryArrayList, request.getParameter(INPUT_VALUE), column, row);
            }
        }
        rewriteExcelFile(categoryArrayList,request,response);
    }

    private ArrayList<Category> addSubcategory(ArrayList<Category> categoryArrayList, Category newSubcategory, int column, int row){
        for(Category category:categoryArrayList){
            if(category.getColumnIndex() == column && category.getRowIndex() == row){
                category.addCategory(newSubcategory);
            }
            if(category.getSubcategoryList()!=null){
                addSubcategory(category.getSubcategoryList(), newSubcategory, column, row);
            }
        }
        return categoryArrayList;
    }

    private ArrayList<Category> deleteCategory(ArrayList<Category> categoryArrayList, int column, int row){
        Iterator<Category> categoryIterator = categoryArrayList.iterator();
        while (categoryIterator.hasNext()){
            Category category = categoryIterator.next();
            if(category.getColumnIndex() == column && category.getRowIndex() == row){
                categoryIterator.remove();
                continue;
            }
            if(category.getSubcategoryList()!=null){
                deleteCategory(category.getSubcategoryList(),column, row);
            }
        }
        return categoryArrayList;
    }

    private ArrayList<Category> editCategory(ArrayList<Category> categoryArrayList, String editableValue, int column, int row){
        for(Category category:categoryArrayList){
            if(category.getColumnIndex() == column && category.getRowIndex() == row){
                category.setCategoryName(editableValue);
            }
            if(category.getSubcategoryList()!=null){
                editCategory(category.getSubcategoryList(), editableValue, column, row);
            }
        }
        return categoryArrayList;
    }

}
