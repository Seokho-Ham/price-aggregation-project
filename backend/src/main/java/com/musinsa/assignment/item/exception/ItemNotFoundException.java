package com.musinsa.assignment.item.exception;

import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import com.musinsa.assignment.common.exception.ApplicationException;

public class ItemNotFoundException extends ApplicationException {

    public ItemNotFoundException() {
        super(ApplicationErrorCode.ITEM_NOT_FOUND);
    }

}
