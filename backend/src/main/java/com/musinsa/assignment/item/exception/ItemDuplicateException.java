package com.musinsa.assignment.item.exception;

import com.musinsa.assignment.common.exception.ApplicationErrorCode;
import com.musinsa.assignment.common.exception.ApplicationException;

public class ItemDuplicateException extends ApplicationException {
    public ItemDuplicateException() {
        super(ApplicationErrorCode.ITEM_DUPLICATE);
    }
}
