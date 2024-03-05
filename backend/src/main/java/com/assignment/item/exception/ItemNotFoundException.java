package com.assignment.item.exception;

import com.assignment.common.exception.ApplicationException;
import com.assignment.common.exception.ApplicationErrorCode;

public class ItemNotFoundException extends ApplicationException {

    public ItemNotFoundException() {
        super(ApplicationErrorCode.ITEM_NOT_FOUND);
    }

}
