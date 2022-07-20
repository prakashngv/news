/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.kisszo.news.netty.http;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.kisszo.news.exceptions.KisszoException;
import com.kisszo.news.netty.controller.CommandController;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;
    /** Buffer that stores the response content */
    private StringBuilder bodyContent  = new StringBuilder();
    private WHttpRequest whttpRequest = null;
    private WHTTPResponse whttpResponse = null;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);
    private static final String FILE_UPLOAD_LOCN = "/tmp";
    private String FILE_UPLOAD_PATH = "";
    private HttpPostRequestDecoder httpDecoder;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("...channelReadComplete called....");
    	this.bodyContent = new StringBuilder();
        ctx.flush();
        
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws IOException {
    	System.out.println("...channelRead0 called....");
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            if (is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            if (request.method() == HttpMethod.POST) {
                httpDecoder = new HttpPostRequestDecoder(factory, request);
                httpDecoder.setDiscardThreshold(0);
              }
        }
        
        
        if (msg instanceof HttpContent) {
        	
           HttpContent httpContent = (HttpContent) msg;
            if(httpContent!=null && httpDecoder!=null && httpDecoder.isMultipart()){
	            httpDecoder.offer(httpContent);
	           System.out.println("Intialize new chunk");
	            readChunk(ctx);
            }else{
            
	            ByteBuf content = httpContent.content();
	            if (content.isReadable()) {
	                this.bodyContent.append(content.toString(CharsetUtil.UTF_8));
	            }
            }

            if (msg instanceof LastHttpContent) {
            	
                LastHttpContent trailer = (LastHttpContent) msg;
                
                if (msg instanceof LastHttpContent) {
                	whttpRequest = new WHttpRequest(request, this.bodyContent);	
                	whttpRequest.setUploadFilePath(FILE_UPLOAD_PATH);
                	resetPostRequestDecoder();
                }
                	whttpResponse = new WHTTPResponse(trailer, ctx, whttpRequest);
                	CommandController controler = CommandController.getInstance();
                	try {
						controler.execute(whttpRequest, whttpResponse);
					} catch (KisszoException e) {
						System.err.println(e.getMessage());
					}
            }
        }
    }
    
    
    private void readChunk(ChannelHandlerContext ctx) throws IOException {
    	System.out.println("...readChunk called....");
    	while (httpDecoder.hasNext()) {
          InterfaceHttpData data = httpDecoder.next();
          if (data != null) {
            try {
              switch (data.getHttpDataType()) {
                case Attribute:
                  break;
                case FileUpload:
                  final FileUpload fileUpload = (FileUpload) data;
                  final File file = new File(FILE_UPLOAD_LOCN + System.nanoTime() + fileUpload.getFilename());
                  FILE_UPLOAD_PATH = file.getAbsolutePath();
                  if (!file.exists()) {
                    file.createNewFile();
                  }
                  System.out.println("Created file " + file.getAbsolutePath());
                  try (FileChannel inputChannel = new FileInputStream(fileUpload.getFile()).getChannel();
                          FileChannel outputChannel = new FileOutputStream(file).getChannel()) {
                       outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                     }
                  break;
              }
            } finally {
              //data.release();
            }
          }
        }
      }
    
    private void resetPostRequestDecoder() {
        request = null;
        if(httpDecoder!=null)
        	httpDecoder.destroy();
        httpDecoder = null;
      }

    private static void send100Continue(ChannelHandlerContext ctx) {
    	System.out.println("...send100Continue called....");
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	System.out.println("...exceptionCaught called....");
        cause.printStackTrace();
        ctx.close();
    }
}
