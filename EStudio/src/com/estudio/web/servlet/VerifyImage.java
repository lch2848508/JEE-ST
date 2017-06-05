package com.estudio.web.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.estudio.utils.ExceptionUtils;

public class VerifyImage extends HttpServlet {

    private static final long serialVersionUID = 577653353609770840L;

    /**
     * Constructor of the object.
     */
    public VerifyImage() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        final ServletOutputStream stream = response.getOutputStream();
        try {
            final String verifyStr = VerifyUtils.registerVerify(request.getParameter("t"), request.getSession());
            verifyImage(verifyStr, stream);
            stream.flush();
        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);
        } finally {
            stream.close();
        }
    }

    @Override
    public void init() throws ServletException {

    }

    /**
     * 生成校验码图片
     * 
     * @param verifyStr
     * @param out
     * @throws IOException
     */
    private static void verifyImage(final String verifyStr, final OutputStream out) throws IOException {
        // 创建内存图像区
        final BufferedImage bi = new BufferedImage(60, 20, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = bi.createGraphics();

        // 设置font
        g.setFont(new Font("Courier New", Font.PLAIN, 12));
        g.setColor(Color.red);
        g.drawString(verifyStr, 2, 15);
        g.dispose();
        bi.flush();
        // 输出到流
        ImageIO.write(bi, "PNG", out);
    }
}
