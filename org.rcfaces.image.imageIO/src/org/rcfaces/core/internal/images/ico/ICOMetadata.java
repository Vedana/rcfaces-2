/*
 * $Id: ICOMetadata.java,v 1.2 2013/11/14 10:55:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.images.ico;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/14 10:55:22 $
 */
public class ICOMetadata extends IIOMetadata {
    private static final String REVISION = "$Revision: 1.2 $";

    public static final String nativeMetadataFormatName = " org_rcfaces_core_internal_images_ico_1.0";

    public int width;

    public int height;

    public byte[] palette = null;

    public int paletteSize;

    private short bitsPerPixel = 8;

    public ICOMetadata() {
        super(true, nativeMetadataFormatName,
                "org.rcfaces.core.internal.images.ico.ICOMetadataFormat", null,
                null);
    }

    public boolean isReadOnly() {
        return true;
    }

    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        }

        if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        }

        throw new IllegalArgumentException(
                "The provided metadata format isn't recognized.");

    }

    private Node getNativeTree() {
        IIOMetadataNode root = new IIOMetadataNode(nativeMetadataFormatName);

        addChildNode(root, "Width", new Integer(width));
        addChildNode(root, "Height", new Integer(height));
        addChildNode(root, "BitsPerPixel", new Short(bitsPerPixel));

        if ((palette != null) && (paletteSize > 0)) {
            IIOMetadataNode node = addChildNode(root, "Palette", null);
            for (int i = 0, j = 0; i < paletteSize; i++) {
                IIOMetadataNode entry = addChildNode(node, "PaletteEntry", null);
                int blue = palette[j++] & 0xff;
                int green = palette[j++] & 0xff;
                int red = palette[j++] & 0xff;
                addChildNode(entry, "Red", new Integer(red));
                addChildNode(entry, "Green", new Integer(green));
                addChildNode(entry, "Blue", new Integer(blue));
            }
        }
        return root;
    }

    public void setFromTree(String formatName, Node root) {
        throw new IllegalStateException("Metadata is read-only.");
    }

    public void mergeTree(String formatName, Node root) {
        throw new IllegalStateException("Metadata is read-only.");
    }

    public void reset() {
        throw new IllegalStateException("Metadata is read-only.");
    }

    private IIOMetadataNode addChildNode(IIOMetadataNode root, String name,
            Object object) {
        IIOMetadataNode child = new IIOMetadataNode(name);
        if (object != null) {
            child.setUserObject(object);
            child.setNodeValue(convertObjectToString(object));
        }
        root.appendChild(child);
        return child;
    }

    protected IIOMetadataNode getStandardChromaNode() {

        IIOMetadataNode node = new IIOMetadataNode("Chroma");

        IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
        subNode.setAttribute("name", "RGB");
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("NumChannels");
        subNode.setAttribute("value", "4"); // Avec Alpha
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("Palette");
        for (int i = 0, j = 0; i < paletteSize; i++) {
            IIOMetadataNode subNode1 = new IIOMetadataNode("PaletteEntry");
            subNode1.setAttribute("index", "" + i);
            subNode1.setAttribute("blue", "" + (palette[j++] & 0xff));
            subNode1.setAttribute("green", "" + (palette[j++] & 0xff));
            subNode1.setAttribute("red", "" + (palette[j++] & 0xff));
            subNode.appendChild(subNode1);
        }

        return node;
    }

    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode node = new IIOMetadataNode("Data");

        IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
        subNode.setAttribute("value", "Index");
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("BitsPerSample");
        subNode.setAttribute("value", "8 8 8");
        node.appendChild(subNode);

        return node;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
        IIOMetadataNode node = null; // scratch node

        // PixelAspectRatio not in image

        node = new IIOMetadataNode("ImageOrientation");
        node.setAttribute("value", "Normal");
        dimension_node.appendChild(node);

        return dimension_node;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        IIOMetadataNode node = new IIOMetadataNode("Transparency");
        IIOMetadataNode subNode = new IIOMetadataNode("Alpha");
        String alpha = "nonpremultiplied";

        subNode.setAttribute("value", alpha);
        node.appendChild(subNode);
        return node;
    }

    public Object clone() {
        ICOMetadata metadata;
        try {
            metadata = (ICOMetadata) super.clone();

        } catch (CloneNotSupportedException e) {
            return null;
        }

        return metadata;
    }

    private static String convertObjectToString(Object obj) {
        if (obj == null) {
            return "";
        }

        String s = "";
        if (obj instanceof byte[]) {
            byte[] bArray = (byte[]) obj;
            for (int i = 0; i < bArray.length; i++) {
                s += bArray[i] + " ";
            }
            return s;
        }

        if (obj instanceof int[]) {
            int[] iArray = (int[]) obj;
            for (int i = 0; i < iArray.length; i++) {
                s += iArray[i] + " ";
            }
            return s;
        }

        if (obj instanceof short[]) {
            short[] sArray = (short[]) obj;
            for (int i = 0; i < sArray.length; i++) {
                s += sArray[i] + " ";
            }
            return s;
        }

        return obj.toString();

    }
}
