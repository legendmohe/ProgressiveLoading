package com.legendmohe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 基于 拓扑排序 的 渐进加载小框架
 */
public class Main {

    public static void main(String[] args) {
        // write your code here
        Context context = new Context();
        PLoading pl = PLoading.create(context);
        PLoading.Node node1 = pl.createNode().onRender(
                new PLoading.RenderListener() {
                    @Override
                    public void onRender(Context context, PLoading.Node node, Runnable callWhenFinished) {
                        System.out.println("node 1 rendering");
                        // 这里显示一些view
                        callWhenFinished.run();
                    }
                }
        );
        PLoading.Node node2 = pl.createNode().onRender(
                new PLoading.RenderListener() {
                    @Override
                    public void onRender(Context context, PLoading.Node node, Runnable callWhenFinished) {
                        System.out.println("node 2 rendering");
                        // 这里显示另外一些view
                        callWhenFinished.run();
                    }
                }
        );
        PLoading.Node node3 = pl.createNode().onRender(
                new PLoading.RenderListener() {
                    @Override
                    public void onRender(Context context, PLoading.Node node, Runnable callWhenFinished) {
                        System.out.println("node 3 rendering");
                        // 这里显示另外一些view
                        callWhenFinished.run();
                    }
                }
        );
        PLoading.Node node4 = pl.createNode().onRender(
                new PLoading.RenderListener() {
                    @Override
                    public void onRender(Context context, PLoading.Node node, Runnable callWhenFinished) {
                        System.out.println("node 4 rendering");
                        // 这里显示另外一些view
                        callWhenFinished.run();
                    }
                }
        );
        PLoading.Node node5 = pl.createNode().onRender(
                new PLoading.RenderListener() {
                    @Override
                    public void onRender(Context context, PLoading.Node node, Runnable callWhenFinished) {
                        System.out.println("node 5 rendering");
                        // 这里显示另外一些view
                        callWhenFinished.run();
                    }
                }
        );
        node1.dependsOn(node2);
        node4.dependsOn(node3);
        node5.dependsOn(node1);
        pl.render();
        /*
        output:
        node 3 rendering
        node 4 rendering
        node 2 rendering
        node 1 rendering
        node 5 rendering
         */
    }

    public static class PLoading {

        private Context mContext;

        private List<Node> mNodes = new ArrayList<Node>();

        private PLoading(Context context) {
            mContext = context;
        }

        public static PLoading create(Context context) {
            return new PLoading(context);
        }

        public synchronized Node createNode() {
            Node newNode = new Node(mContext);
            mNodes.add(newNode);
            return newNode;
        }

        public synchronized void render() {
            if (mNodes.size() > 0) {
                renderInternal(new HashSet<Node>(mNodes));
            }
        }

        //////////////////////////////////////////////////////////////////////

        /*
         *拓扑排序（Topological Sorting）
         *
         * TODO - 怎么样能效率高点？DFS+环路检测？
         */
        private void renderInternal(final Set<Node> pendingSet) {
            // 处理完毕
            if (pendingSet.size() <= 0) {
                return;
            }
            LinkedList<Node> zeroSet = new LinkedList<Node>();
            Iterator<Node> it = pendingSet.iterator();
            while (it.hasNext()) {
                Node node = it.next();
                if (node.dependencies.size() == 0) {
                    zeroSet.add(node);
                    it.remove();
                    // 去掉其他node中这个依赖
                    for (Node pendingNode : pendingSet) {
                        pendingNode.dependencies.remove(node);
                    }
                    // 拿一个出来就好了
                    break;
                }
            }
            if (zeroSet.size() > 0) {
                Node first = zeroSet.removeFirst();
                first.fireRender(new Runnable() {
                    @Override
                    public void run() {
                        renderInternal(pendingSet);
                    }
                });
            } else {
                throw new IllegalStateException("loop exist!");
            }
        }

        //////////////////////////////////////////////////////////////////////

        public static class Node {
            private Set<Node> dependencies = new HashSet<Node>();
            private Context context;
            private RenderListener listener;

            private Node(Context context) {
                this.context = context;
            }

            public void fireRender(Runnable callWhenFinished) {
                if (listener != null) {
                    listener.onRender(context, this, callWhenFinished);
                }
            }

            public Node dependsOn(Node node) {
                dependencies.add(node);
                return this;
            }

            public Node onRender(RenderListener listener) {
                this.listener = listener;
                return this;
            }

            //////////////////////////////////////////////////////////////////////
        }

        public interface RenderListener {
            void onRender(Context context, Node node, Runnable callWhenFinished);
        }
    }

    public static class Context {

    }
}
