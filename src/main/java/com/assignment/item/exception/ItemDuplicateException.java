package com.assignment.item.exception;

import com.assignment.common.exception.ApplicationErrorCode;
import com.assignment.common.exception.ApplicationException;

public class ItemDuplicateException extends ApplicationException {
    public ItemDuplicateException() {
        super(ApplicationErrorCode.ITEM_DUPLICATE);
    }
}
