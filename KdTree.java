import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Queue;

import java.util.Iterator;

public class KdTree
{
    private Node root;
    private int n;

    public KdTree()
    {
        root = null;
        n = 0;
    }
    public boolean isEmpty()
    {
        return n == 0;
    }
    public int size()
    {
        return n;
    }

    public boolean contains(Point2D p)
    {
        double x = p.x();
        double y = p.y();
        Node r = root;
        while (true)
        {
            if (r == null)
                return false;
            if (r.x() == x && r.y() == y)
                return true;
            if (r.vert())
            {
                if (x < r.x())
                    r = r.left;
                else
                    r = r.right;
            }
            else
            {
                if (y < r.y())
                    r = r.left;
                else
                    r = r.right;
            }
        }
    }

    public void insert(Point2D p)
    {
        Node ins = new Node(p.x(), p.y());
        if (contains(p))
            return;
        root = insert(root, ins, true);
        n++;
    }
    private Node insert(Node r, Node ins, boolean toSetVert)
    {
        if (r == null)
        {
            ins.changeVert(toSetVert);
            return ins;
        }
        int cmp = r.compareTo(ins);
        if (cmp <= 0)
            r.right = insert(r.right, ins, !toSetVert);
        else
            r.left = insert(r.left, ins, !toSetVert);
        return r;
    }

    public void draw()
    {
        draw(0, 1, 0, 1, root);
    }
    private void draw(double limitLeftForVert, double limitRightForVert, double limitLeftForHor, double limitRightForHor, Node r)
    {
        if (r == null)
            return;
        if (r.vert())
        {
            StdDraw.setPenColor(0, 0, 0);
            StdDraw.point(r.x(), r.y());
            StdDraw.setPenColor(255, 0, 0);
            StdDraw.line(r.x(), limitLeftForVert, r.x(), limitRightForVert);
            draw(limitLeftForVert, limitRightForVert, limitLeftForHor, r.x(), r.left);
            draw(limitLeftForVert, limitRightForVert, r.x(), limitRightForHor, r.right);
        }
        else
        {
            StdDraw.setPenColor(0, 0, 0);
            StdDraw.point(r.x(), r.y());
            StdDraw.setPenColor(0, 0, 255);
            StdDraw.line(limitLeftForHor, r.y(), limitRightForHor, r.y());
            draw(limitLeftForVert, r.y(), limitLeftForHor, limitRightForHor, r.left);
            draw(r.y(), limitRightForVert, limitLeftForHor, limitRightForHor, r.right);
        }
    }

    private class Node implements Comparable<Node>
    {
        private final double x;
        private final double y;
        private Node left;
        private Node right;
        private boolean vert;       //true if vertical, false if horizontal

        private Node(double x, double y)
        {
            this.x = x;
            this.y = y;
            left = null;
            right = null;
        }
        public void changeLeft(Node left)
        {
            this.left = left;
        }
        public void changeRight(Node right)
        {
            this.right = right;
        }
        public void changeVert(boolean vert)
        {
            this.vert = vert;
        }
        public double x()
        {
            return x;
        }
        public double y()
        {
            return y;
        }
        public boolean vert()
        {
            return vert;
        }
        public int compareTo(Node o)
        {
            if (vert)
            {
                if (x < o.x)
                    return -1;
                else if (x > o.x)
                    return 1;
                else
                    return 0;
            }
            if (y < o.y)
                return -1;
            else if (y > o.y)
                return 1;
            else
                return 0;
        }
    }

    public Iterable<Point2D> range(RectHV rect)
    {
        return new KdTreeIterable(rect);
    }
    private class KdTreeIterable implements Iterable<Point2D>
    {
        private final RectHV rect;
        KdTreeIterable(RectHV rect)
        {
            this.rect = rect;
        }
        public Iterator<Point2D> iterator()
        {
            return new KdTreeIterator(rect);
        }
        private class KdTreeIterator implements Iterator<Point2D>
        {
            private final RectHV rect;
            private final Queue<Point2D> q;
            Iterator<Point2D> it;
            public KdTreeIterator(RectHV rect)
            {
                this.rect = rect;
                q = new Queue<Point2D>();
                createQueue(root);
                it = q.iterator();
            }
            private void createQueue(Node r)
            {
                if (r == null)
                    return;
                Point2D p = new Point2D(r.x(), r.y());
                if (rect.contains(p))
                    q.enqueue(p);
                if (r.vert)
                {
                    if (rect.xmax() < r.x())
                        createQueue(r.left);
                    else if (rect.xmin() > r.x())
                        createQueue(r.right);
                    else
                    {
                        createQueue(r.left);
                        createQueue(r.right);
                    }
                }
                else
                {
                    if (rect.ymax() < r.y())
                        createQueue(r.left);
                    else if (rect.ymin() > r.y())
                        createQueue(r.right);
                    else
                    {
                        createQueue(r.left);
                        createQueue(r.right);
                    }
                }
            }

            public boolean hasNext()
            {
                return it.hasNext();
            }

            public Point2D next()
            {
                return it.next();
            }

            public void remove()
            {
                throw new UnsupportedOperationException("UnsupportedOperationException");
            }
        }
    }

    public Point2D nearest(Point2D p)
    {
        Node r = nearest(root, p, null, 10.0);
        if (r == null)
            return null;
        return new Point2D(r.x(), r.y());
    }
    private Node nearest(Node r, Point2D p, Node curMin, double minDist)
    {
        if (r == null)
            return curMin;
        double dist = p.distanceTo(new Point2D(r.x(), r.y()));
        if (curMin == null || dist < minDist)
        {
            curMin = r;
            minDist = dist;
        }
        int cmp = r.compareTo(new Node(p.x(), p.y()));
        if (cmp > 0)
        {
            curMin = nearest(r.left, p, curMin, minDist);
            minDist = p.distanceTo(new Point2D(curMin.x(), curMin.y()));
            if (r.vert)
                dist = Math.abs(p.x() - r.x());
            else
                dist = Math.abs(p.y() - r.y());

            if (dist < minDist)
                curMin = nearest(r.right, p, curMin, minDist);
        }
        else
        {
            curMin = nearest(r.right, p, curMin, minDist);
            minDist = p.distanceTo(new Point2D(curMin.x(), curMin.y()));
            if (r.vert)
                dist = Math.abs(p.x() - r.x());
            else
                dist = Math.abs(p.y() - r.y());

            if (dist < minDist)
                curMin = nearest(r.left, p, curMin, minDist);
        }
        return curMin;
    }

    public static void main(String[] args)
    {
        KdTree kd = new KdTree();
        /*kd.insert(new Point2D(0.7, 0.2));
        kd.insert(new Point2D(0.5, 0.4));
        kd.insert(new Point2D(0.2, 0.3));
        kd.insert(new Point2D(0.4, 0.7));
        kd.insert(new Point2D(0.9, 0.6));*/
        /*kd.insert(new Point2D(0.5, 0.75));
        kd.insert(new Point2D(0.25, 1.0));
        kd.insert(new Point2D(0.0, 0.0));
        kd.insert(new Point2D(0.0, 0.5));
        kd.insert(new Point2D(1.0, 0.0));
        kd.insert(new Point2D(0.5, 1.0));
        kd.insert(new Point2D(0.5, 0.0));
        kd.insert(new Point2D(0.5, 0.5));
        System.out.println(kd.size());
        System.out.println(kd.root.right.right.right.x() + " " + kd.root.right.right.right.y());*/
        kd.insert(new Point2D(0.7, 0.2));
        kd.insert(new Point2D(0.5, 0.4));
        kd.insert(new Point2D(0.2, 0.3));
        kd.insert(new Point2D(0.4, 0.7));
        kd.insert(new Point2D(0.9, 0.6));
        System.out.println(kd.nearest(new Point2D(0.692, 0.896)).toString());

    }
}
