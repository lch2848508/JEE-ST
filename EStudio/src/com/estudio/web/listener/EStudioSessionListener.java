package com.estudio.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.estudio.impl.service.sercure.HttpSessionService;

public class EStudioSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(final HttpSessionEvent event) {
        HttpSessionService.getInstance().put(event.getSession());
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        HttpSessionService.getInstance().del(event.getSession().getId());
    }

}
