import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PointSET
{
    private final SET<Point2D> points;
    private int n;

    public PointSET()
    {
        points = new SET<Point2D>();
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
    public void insert(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("IllegalArgumentException");
        if (!contains(p))
        {
            n++;
            points.add(p);
        }
    }
    public boolean contains(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("IllegalArgumentException");
        return points.contains(p);
    }
    public void draw()
    {
        Iterator<Point2D> it = points.iterator();
        while (it.hasNext())
        {
            Point2D p = it.next();
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect)
    {
        if (rect == null)
            throw new IllegalArgumentException("IllegalArgumentException");
        return new RectRangeIterable(rect);
    }
    private class RectRangeIterable implements Iterable<Point2D>
    {
        private final RectHV rect;
        public RectRangeIterable(RectHV rect)
        {
            this.rect = rect;
        }
        public Iterator<Point2D> iterator()
        {
            return new RectRangeIterator(rect);
        }

        private class RectRangeIterator implements Iterator<Point2D>
        {
            Iterator<Point2D> it = points.iterator();
            private final RectHV rect;
            Point2D current;

            public RectRangeIterator(RectHV rect)
            {
                this.rect = rect;
                findNext();
            }
            private void findNext()
            {
                int flag = 0;
                while (it.hasNext())
                {
                    Point2D p = it.next();
                    if (rect.contains(p))
                    {
                        flag = 1;
                        current = p;
                        break;
                    }
                }
                if (flag == 0)
                    current = null;
            }
            public boolean hasNext()
            {
                return current != null;
            }
            public Point2D next()
            {
                //if (current == null)
                //    throw new NoSuchElementException("ReachedEnd");
                Point2D toReturn = current;
                findNext();
                return toReturn;
            }
            public void remove()
            {
                throw new UnsupportedOperationException("UnsupportedOperationException");
            }
        }
    }
    public Point2D nearest(Point2D p)
    {
        if (p == null)
            throw new IllegalArgumentException("IllegalArgumentException");
        Iterator<Point2D> it = points.iterator();
        double dist = 10.0;
        Point2D pmin = null;
        while (it.hasNext())
        {
            Point2D p2 = it.next();
            double d = p.distanceTo(p2);
            if (d < dist)
            {
                pmin = p2;
                dist = d;
            }
        }
        return pmin;
    }

    public static void main(String[] args)
    {
        RectHV rect = new RectHV(0.0, 0.0, 0.25, 0.25);
        PointSET ps = new PointSET();
        ps.insert(new Point2D(0.5, 0.5));
        ps.insert(new Point2D(0.0, 0.0));
        System.out.println(ps.size());
        Iterator<Point2D> it = ps.range(rect).iterator();
        while (it.hasNext())
        {
            Point2D p = it.next();
            System.out.println(p.x() + " " + p.y());
        }
    }
}
