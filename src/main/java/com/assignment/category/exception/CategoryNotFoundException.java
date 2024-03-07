package com.assignment.category.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class CategoryNotFoundException extends ApplicationException {

    public CategoryNotFoundException() {
        super(ApplicationErrorCode.CATEGORY_NOT_FOUND);
    }
}
