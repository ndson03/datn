package com.ndson03.quanlykhoahoc.service.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class ToastService implements Serializable {

    private List<Toast> toasts = new ArrayList<>();

    public void addInfoToast(String message) {
        toasts.add(new Toast("info", message));
    }

    public void addSuccessToast(String message) {
        toasts.add(new Toast("success", message));
    }

    public void addWarningToast(String message) {
        toasts.add(new Toast("warning", message));
    }

    public void addErrorToast(String message) {
        toasts.add(new Toast("error", message));
    }

    public List<Toast> getToasts() {
        List<Toast> currentToasts = new ArrayList<>(toasts);
        toasts.clear();
        return currentToasts;
    }

    public boolean hasToasts() {
        return !toasts.isEmpty();
    }

    public static class Toast {
        private String type;
        private String message;

        public Toast(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }
}