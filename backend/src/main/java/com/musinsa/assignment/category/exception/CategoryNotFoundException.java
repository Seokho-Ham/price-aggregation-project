package com.musinsa.assignment.category.exception;

import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import com.musinsa.assignment.common.exception.ApplicationException;

public class CategoryNotFoundException extends ApplicationException {

    public CategoryNotFoundException() {
        super(ApplicationErrorCode.CATEGORY_NOT_FOUND);
    }
}
