import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {

	static Channel ch = null;

	public final static ThreadPoolExecutor THREAD_EXCUTER = new ThreadPoolExecutor(500, 500, 10000,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	public void run(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("http-codec", new HttpServerCodec())
									.addLast("aggregator", new HttpObjectAggregator(65535))
									.addLast("http-chunked", new ChunkedWriteHandler())
									.addLast("handler", new WebSocketServerHandler());
						}
					});
			ChannelFuture future = boot.bind("localhost", 8080).sync();
			THREAD_EXCUTER.execute(() -> {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						System.out.println("run>>");
						if (ch != null) {
							ch.writeAndFlush(new TextWebSocketFrame("welcome,now:" + LocalDateTime.now().toString()));
						}
					}
				}, 0, 5000);
			});

			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

		private WebSocketServerHandshaker shaker;

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("channelActive");
			ch = ctx.channel();
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			ch = null;
		}

		@Override
		protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof FullHttpRequest) {
				handlerHttpRequest(ctx, (FullHttpRequest) msg);
			} else if (msg instanceof WebSocketFrame) {
				handlerWebSocketRequest(ctx, (WebSocketFrame) msg);
			}
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		private void handlerWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame msg) {

			if (msg instanceof CloseWebSocketFrame) {
				shaker.close(ctx.channel(), (CloseWebSocketFrame) msg.retain());
				return;
			}
			if (msg instanceof PingWebSocketFrame) {
				ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
			}

		}

		private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
			if (!msg.decoderResult().isSuccess() || (!"websocket".equals(msg.headers().get("Upgrade")))) {

				return;
			}
			WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
					"ws//localhost:8080/websocket", null, false);
			shaker = factory.newHandshaker(msg);

			if (shaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				shaker.handshake(ctx.channel(), msg);
			}
		}

	}

	public static void main(String[] args) {
		new WebSocketServer().run(8080);
	}
}
