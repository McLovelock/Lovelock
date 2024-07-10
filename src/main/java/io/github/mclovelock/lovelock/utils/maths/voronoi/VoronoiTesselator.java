package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.List;

public final class VoronoiTesselator {

    private static VoronoiResult voronoi(SiteList siteList) {
        var result = new VoronoiResult();

        var edgeList = new EdgeList(siteList.getXmin(), siteList.getXmax(), siteList.size());
        var priorityQ = new PriorityQueue(siteList.getYmin(), siteList.getYmax(), siteList.size());
        var siteIter = siteList.iterator();

        VoronoiSite bottomSite = siteIter.next();
        result.outSite(bottomSite);
        VoronoiSite newSite = siteIter.next();
        VoronoiSite minPt = new SiteImpl(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        while (true) {
            if (!priorityQ.isEmpty())
                minPt = priorityQ.getMinPt();

            if ((newSite != null) && (priorityQ.isEmpty() || (newSite.compareTo(minPt) < 0))) {
                result.outSite(newSite);

                HalfEdge lbnd = edgeList.leftbnd(newSite);
                HalfEdge rbnd = lbnd.right;

                VoronoiSite bot = lbnd.rightreg(bottomSite);
                Edge edge = Edge.bisect(bot, newSite);
                result.outBisector(edge);

                HalfEdge bisector = new HalfEdge(edge, Edge.LEFT);
                edgeList.insert(lbnd, bisector);

                VoronoiSite p = lbnd.intersect(bisector);
                if (p != null) {
                    priorityQ.delete(lbnd);
                    priorityQ.insert(lbnd, p, newSite.distance(p));
                }

                lbnd = bisector;
                bisector = new HalfEdge(edge, Edge.RIGHT);
                edgeList.insert(lbnd, bisector);

                p = bisector.intersect(rbnd);
                if (p != null)
                    priorityQ.insert(bisector, p, newSite.distance(p));

                newSite = siteIter.next();
            } else if (!priorityQ.isEmpty()) {
                HalfEdge lbnd = priorityQ.popMinHalfEdge();
                HalfEdge llbnd = lbnd.left;
                HalfEdge rbnd = lbnd.right;
                HalfEdge rrbnd = rbnd.right;

                VoronoiSite bot = lbnd.leftreg(bottomSite);
                VoronoiSite top = rbnd.rightreg(bottomSite);

                VoronoiSite mid = lbnd.rightreg(bottomSite);
                result.outTriple(bot, top, mid);

                VoronoiSite v = lbnd.vertex;
                siteList.setSiteNumber(v);
                result.outVertex(v);

                if (lbnd.edge.setEndpoint(lbnd.primarySite, v))
                    result.outEdge(lbnd.edge);

                if (rbnd.edge.setEndpoint(rbnd.primarySite, v))
                    result.outEdge(rbnd.edge);

                edgeList.delete(lbnd);
                priorityQ.delete(rbnd);
                edgeList.delete(rbnd);

                int pm = Edge.LEFT;
                if (bot.siteY() > top.siteY()) {
                    var tmp = top;
                    top = bot;
                    bot = tmp;
                    pm = Edge.RIGHT;
                }

                Edge edge = Edge.bisect(bot, top);
                result.outBisector(edge);

                HalfEdge bisector = new HalfEdge(edge, pm);

                edgeList.insert(llbnd, bisector);
                if (edge.setEndpoint(Edge.RIGHT - pm, v))
                    result.outEdge(edge);

                VoronoiSite p = llbnd.intersect(bisector);
                if (p != null) {
                    priorityQ.delete(llbnd);
                    priorityQ.insert(llbnd, p, bot.distance(p));
                }

                p = bisector.intersect(rrbnd);
                if (p != null)
                    priorityQ.insert(bisector, p, bot.distance(p));
            } else {
                break;
            }
        }

        HalfEdge he = edgeList.getLeftEnd().right;
        while (he != edgeList.getRightEnd()) {
            result.outEdge(he.edge);
            he = he.right;
        }
        Edge.nEdges = 0;

        return result;
    }

    public static VoronoiResult computeVoronoiDiagram(List<VoronoiSite> points) {
        var siteList = new SiteList(points);
        return voronoi(siteList);
    }

}
