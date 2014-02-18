package org.rcfaces.renderkit.svg.internal.image;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.rcfaces.renderkit.svg.internal.util.Symplifier;
import org.rcfaces.renderkit.svg.item.INodeItem;
import org.w3c.dom.Element;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 15:52:39 $
 */
class AdapterImageTranscoder extends ImageTranscoder {
    private static final double DEFAULT_FLATNESS = 0.0;

    private static final double DEFAULT_DISTANCE_TOLERANCE = 0.0;

    private Map<String, INodeItem> selectables;

    private Map<INodeItem, ShapeValue> selectableShapes;

    private GraphicsNode rootGraphicsNode;

    private BridgeContext bridgeContext;

    private CanvasGraphicsNode canvasGraphicsNode;

    private AffineTransform globalTransform;

    private double curveFlatness = DEFAULT_FLATNESS;

    private double distanceTolerance = DEFAULT_DISTANCE_TOLERANCE;

    @Override
    public BufferedImage createImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        return img;
    }

    public void setDefaultFontFamily(String fontName) {
        addTranscodingHint(SVGAbstractTranscoder.KEY_DEFAULT_FONT_FAMILY,
                fontName);
    }

    public void setPixelUnitToMillimeter(float pixelUnit) {
        addTranscodingHint(SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER,
                new Float(pixelUnit));
    }

    public void setCurveFlatness(double flatness) {
        this.curveFlatness = flatness;
    }

    public void setDistanceTolerance(double distanceTolerance) {
        this.distanceTolerance = distanceTolerance;
    }

    public void setImageHeight(int imageHeight) {
        addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, new Float(
                imageHeight));
    }

    public void setImageWidth(int imageWidth) {
        addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, new Float(
                imageWidth));
    }

    public void setSelectables(Map<String, INodeItem> selectables) {
        this.selectables = selectables;

        addTranscodingHint(SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD,
                Boolean.TRUE);
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output)
            throws TranscoderException {

        ((AdapterTranscoderOuput) output).setBufferedImage(img);
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    @Override
    public void transcode(TranscoderInput input, TranscoderOutput output)
            throws TranscoderException {

        super.transcode(input, output);

        globalTransform = canvasGraphicsNode.getGlobalTransform();
    }

    @Override
    protected CanvasGraphicsNode getCanvasGraphicsNode(GraphicsNode gn) {
        // Gros hack ... mais a t-on le choix ?

        canvasGraphicsNode = super.getCanvasGraphicsNode(gn);

        if (selectables != null && selectables.isEmpty() == false) {
            selectableShapes = new HashMap<INodeItem, ShapeValue>(
                    selectables.size());

            searchSelectableGraphicsNode(gn);
        }

        return canvasGraphicsNode;
    }

    @Override
    protected BridgeContext createBridgeContext(String svgVersion) {
        BridgeContext bridgeContext = super.createBridgeContext(svgVersion);

        this.bridgeContext = bridgeContext;

        return bridgeContext;
    }

    private void searchSelectableGraphicsNode(GraphicsNode root) {

        List<GraphicsNode> l = new ArrayList<GraphicsNode>(64);

        l.add(root);

        for (; l.isEmpty() == false;) {
            GraphicsNode node = l.remove(l.size() - 1);

            Element element = bridgeContext.getElement(node);

            if (element != null) {
                String id = element.getAttribute("id");
                if (id != null) {
                    INodeItem item = selectables.get(id);
                    if (item != null) {
                        Shape shape = node.getOutline();
                        if (shape != null) {
                            if (distanceTolerance > 0.0) {
                                shape = Symplifier
                                        .simplifyShape(distanceTolerance,
                                                curveFlatness, shape);

                            } else if (curveFlatness > 0.0) {

                                GeneralPath generalPath = new GeneralPath();
                                PathIterator pathIterator = shape
                                        .getPathIterator(null, curveFlatness);
                                generalPath.setWindingRule(pathIterator
                                        .getWindingRule());
                                generalPath.append(pathIterator, false);

                                shape = generalPath;
                            }

                            selectableShapes.put(item, new ShapeValue(shape,
                                    item));
                        }
                    }
                }
            }

            if (node instanceof CompositeGraphicsNode) {
                CompositeGraphicsNode compositeGraphicsNode = (CompositeGraphicsNode) node;

                @SuppressWarnings("unchecked")
                List<GraphicsNode> children = compositeGraphicsNode
                        .getChildren();

                l.addAll(children);
            }
        }
    }

    public final AffineTransform getGlobalTransform() {
        return globalTransform;
    }

    public final Map<INodeItem, ShapeValue> getSelectableShapes() {
        if (selectableShapes == null) {
            return Collections.emptyMap();
        }
        return selectableShapes;
    }
}