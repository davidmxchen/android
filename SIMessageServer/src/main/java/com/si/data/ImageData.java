/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.si.data;

/**
 *
 * @author Mingxing Chen
 */
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.xml.bind.DatatypeConverter;

/**
 * A class containing static convenience methods for converting(encoding) image file to String to be transmitted
 * and performing simple decoding image from String.
 */

public final class ImageData {

    
    /**
     * Get a String encoding of image from File/InputStream
     * @param inputStream
     * @param formatName, the MIME type of image file, eg. jpg, gif, png
     * @return Image encoded in String
     * @throws IOException 
     */
    public static String getImageInString(FileInputStream inputStream, String formatName) throws IOException{
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        BufferedImage img=ImageIO.read(inputStream);
        
        ImageIO.write(img, "jpg", baos);
        baos.flush();

        String encodedString = DatatypeConverter.printBase64Binary(baos.toByteArray());
        System.out.println("encoded image string is: " + encodedString);
        // in JAVA 8 use Base64 instead
        // String base64String=Base64.encode(baos.toByteArray());
        baos.close();
        return encodedString;
    }
    
    /**
     * Write encoded image String to file
     * @param imageString which is encoded from image file
     * @param formatName, the MIME type of image file, eg. jpg, gif, png
     * @param file, provided by use to write to
     * @throws IOException 
     */
    public static void writeImageStringToFile(String imageString, String formatName, File file) throws IOException{
         byte[] decodedByte = DatatypeConverter.parseBase64Binary(imageString);
        //byte[] bytearray = Base64.decode(base64String);

        BufferedImage imag=ImageIO.read(new ByteArrayInputStream(decodedByte));
        ImageIO.write(imag, formatName, file);
    }
}
