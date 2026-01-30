package com.finance.api.application.usecases.user;

import com.finance.api.application.gateways.PasswordEncoderGateway;
import com.finance.api.application.gateways.UserGateway;
import com.finance.api.domain.user.User;
import com.finance.api.domain.user.UserRole;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.Iterator;

public class ImportUsersFromExcel {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoder;

    public ImportUsersFromExcel(UserGateway userGateway, PasswordEncoderGateway passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(InputStream fileInputStream) {
        try (Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String name = getCellValue(row.getCell(0));
                String email = getCellValue(row.getCell(1));
                String rawPassword = getCellValue(row.getCell(2));
                String cpf = getCellValue(row.getCell(3));

                if (email != null && !email.isBlank() && rawPassword != null) {

                    if (!userGateway.existsByEmail(email)) {
                        String encodedPassword = passwordEncoder.encode(rawPassword);

                        User newUser = new User(
                                cpf,
                                name,
                                email,
                                encodedPassword,
                                UserRole.USER
                        );

                        userGateway.createUser(newUser);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }
}