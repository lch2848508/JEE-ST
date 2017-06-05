package com.estudio.web.servlet.message;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.estudio.context.GlobalContext;
import com.estudio.define.sercure.ClientLoginInfo;

public final class MessageService {
    private final LinkedBlockingQueue<MessageItem> messages = new LinkedBlockingQueue<MessageItem>(); // 消息队列
    private final ReentrantLock asyncContextLock = new ReentrantLock();
    private final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();// 保持连接时间
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    // private List<AsyncContextItem> asyncContextItems = new
    // ArrayList<MessageService.AsyncContextItem>();

    /**
     * 
     * @author ShengHongL
     * 
     */
    // private class AsyncContextItem {
    // AsyncContext context; // 异步对象
    // long userId; // 用户ID
    // String sessionId;
    //
    // public AsyncContextItem(AsyncContext context, long userId, String
    // sessionId) {
    // super();
    // this.context = context;
    // this.userId = userId;
    // this.sessionId = sessionId;
    // }
    // }

    /**
     * 构造函数
     */
    private MessageService() {
        // for (int i = 0; i < 4096; i++)
        // blankStr4096 += " ";
        // for (int i = 0; i < THREAD_POOL_SIZE; i++)
        // executor.execute(new ThreadProc());
    }

    /**
     * 获取设备上下文列表
     * 
     * @param list
     * @return
     */
    // private List<AsyncContext> getAnyncContextByUsers(List<Long> list) {
    // List<AsyncContext> result = new ArrayList<AsyncContext>();
    // asyncContextLock.lock();
    // try {
    // for (int i = 0; i < asyncContextItems.size(); i++) {
    // AsyncContextItem item = asyncContextItems.get(i);
    // if (list == null || list.indexOf(item.userId) != -1)
    // result.add(item.context);
    // }
    // } finally {
    // asyncContextLock.unlock();
    // }
    // return result;
    // }

    /**
     * 注册设备上下文
     * 
     * @param userId
     * @param context
     */
    // private void registerAsyncContext(AsyncContext context) {
    // asyncContextLock.lock();
    // try {
    // HttpSession session = ((HttpServletRequest)
    // context.getRequest()).getSession();
    // ClientLoginInfo loginInfo =
    // RuntimeContext.getClientLoginService().getLoginInfo(session);
    // if (loginInfo != null)
    // asyncContextItems.add(new AsyncContextItem(context, loginInfo.getId(),
    // session.getId()));
    // } finally {
    // asyncContextLock.unlock();
    // }
    // }

    /**
     * 反注册设备上下文
     * 
     * @param userId
     * @param context
     */
    // private void unregisterAsyncContext(AsyncContext context) {
    // asyncContextLock.lock();
    // try {
    // for (int i = asyncContextItems.size() - 1; i >= 0; i--) {
    // if (asyncContextItems.get(i).context == context) {
    // asyncContextItems.remove(i);
    // break;
    // }
    // }
    // } finally {
    // asyncContextLock.unlock();
    // }
    // }

    /**
     * 反注册设备上下文
     * 
     * @param userId
     * @param context
     */
    // private void unregisterAsyncContext(HttpSession session) {
    // AsyncContext context = null;
    // asyncContextLock.lock();
    // try {
    // String sessionId = session.getId();
    // for (int i = asyncContextItems.size() - 1; i >= 0; i--) {
    // if (asyncContextItems.get(i).sessionId.equals(sessionId)) {
    // context = asyncContextItems.get(i).context;
    // asyncContextItems.remove(i);
    // break;
    // }
    // }
    // } finally {
    // asyncContextLock.unlock();
    // }
    // if (context != null)
    // context.complete();
    // }

    /**
     * 线程处理函数
     * 
     * @author ShengHongL
     * 
     */
    // private class ThreadProc implements Runnable {
    // // @Override
    // // public void run() {
    // // while (true) {
    // // try {
    // // MessageItem item = messages.take();
    // // processMessage(item);
    // // } catch (final Exception e) { ExceptionUtils.loggerException(e,con);
    // // ExceptionUtils.printExceptionTrace(e);
    // // }
    // // }
    // }

    /**
     * 处理消息
     * 
     * @param messageItem
     * @throws IOException
     */
    // private void processMessage(MessageItem messageItem) throws IOException {
    // List<AsyncContext> list =
    // getAnyncContextByUsers(messageItem.isBroadcast() ? null :
    // messageItem.getRecivers());
    // for (int i = 0; i < list.size(); i++) {
    // try {
    // AsyncContext context = list.get(i);
    // synchronized (context) {
    // context.getResponse().getWriter().println("<script>p(" +
    // messageItem.toJSON() +
    // ");</script>");
    // context.getResponse().flushBuffer();
    // }
    // } catch (final Exception e) { ExceptionUtils.loggerException(e,con);
    // ExceptionUtils.printExceptionTrace(e);
    // }
    // }
    // }
    // }

    /**
     * 处理消息消息
     * 
     * @param messageItem
     * @throws IOException
     */

    /**
     * 监听器
     * 
     * @author ShengHongL
     * 
     */
    // private class AsyncListenerImpl implements AsyncListener {
    // private AsyncContext context;
    //
    // public AsyncListenerImpl(AsyncContext context) {
    // this.context = context;
    // }
    //
    // @Override
    // public void onComplete(AsyncEvent arg0) throws IOException {
    // unregisterAsyncContext(context);
    // }

    // @Override
    // public void onError(AsyncEvent arg0) throws IOException {
    // arg0.getAsyncContext().complete();
    // }
    //
    // @Override
    // public void onStartAsync(AsyncEvent arg0) throws IOException {
    // }
    //
    // @Override
    // public void onTimeout(AsyncEvent arg0) throws IOException {
    // context.getResponse().getWriter().println("<script>t();</script>");
    // context.getResponse().flushBuffer();
    // }
    // }

    /**
     * 添加异步任务
     * 
     * @param request
     * @param response
     * @param session
     * @throws IOException
     */
    public void addAsync(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        // response.getWriter().println("<script>var p=window.parent.comet.processContent;t=window.parent.comet.timeout;</script>");
        // response.getWriter().println(blankStr4096);
        // response.getWriter().println();
        // AsyncContext context = request.startAsync(request, response);
        // context.setTimeout(keepAliveSecond * 1000);
        // context.addListener(new AsyncListenerImpl(context));
        // registerAsyncContext(context);
    }

    /**
     * 关闭服务器连接
     * 
     * @param session
     */
    public void closeMessageClient(final HttpSession session) {
        // asyncContextLock.lock();
        // try {
        // unregisterAsyncContext(session);
        // } finally {
        // asyncContextLock.unlock();
        // }
    }

    /**
     * 发送消息
     * 
     * @param sendUserId
     * @param sendUserName
     * @param reciverUsers
     * @param content
     */
    public void sendMessage(final long sendUserId, final String sendUserName, final List<Long> reciverUsers, final Object content) {
        // MessageItem messageItem = new MessageItem();
        // messageItem.setSendUserId(sendUserId);
        // messageItem.setSendUserName(sendUserName);
        // messageItem.setContent(content);
        // messageItem.getRecivers().addAll(reciverUsers);
        // messages.add(messageItem);
    }

    /**
     * 广播消息
     * 
     * @param sendUserId
     * @param sendUserName
     * @param content
     */
    public void broadcasdMessage(final String content) {
        final ClientLoginInfo loginInfo = GlobalContext.getLoginInfo();
        final long sendUserId = loginInfo.getId();
        final String sendUserName = loginInfo.getRealName();
        final MessageItem messageItem = new MessageItem();
        messageItem.setSendUserId(sendUserId);
        messageItem.setSendUserName(sendUserName);
        messageItem.setContent(content);
        messageItem.setBroadcast(true);
        messages.add(messageItem);
    }

    private static final MessageService INSTANCE = new MessageService();

    public static MessageService getInstance() {
        return INSTANCE;
    }

}
