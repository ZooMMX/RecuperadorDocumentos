/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion.exceptions;

/**
 *
 * @author mora
 */
public class UEHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread th, Throwable ex) {
        ExceptionWindow.show(ex);
        
    }
}
