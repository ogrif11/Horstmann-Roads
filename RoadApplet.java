import java.awt.*;
import java.applet.*;

public class RoadApplet extends Applet implements Runnable
{  
   public void init()
   {  
      canvas = new RoadCanvas();
      slowdown = new Scrollbar(Scrollbar.HORIZONTAL, 0, 0, 0, 100);
      slowdown.setPageIncrement(10);
      slowdown.setValue(10);
     
      arrival = new Scrollbar(Scrollbar.HORIZONTAL, 0, 0, 0, 100);
      arrival.setPageIncrement(10);
      arrival.setValue(50);
      
  
      Panel p = new Panel();
      p.setLayout(new GridLayout(1, 6));
      p.add(new Label("Slowdown"));
      p.add(slowdown);
      p.add(new Label(""));
      p.add(new Label("Arrival"));
      p.add(arrival);
      p.add(new Label("")); 
      setLayout(new BorderLayout());
      add("North", p);
      add("Center", canvas);
   }
   
   public double getSlowdown() 
   {  
      return 0.01 * slowdown.getValue();
   }
   
   public double getArrival()
   {  
      return 0.01 * arrival.getValue();

   }
           
   public void run()
   {  
      for(;;)
      {  
         canvas.update(getSlowdown(), getArrival());
         try { Thread.sleep(50); } catch(InterruptedException e) {}
      }
   }

   public void start()
   {
      runner = new Thread(this);
      runner.start();
   }
  
   private RoadCanvas canvas;
   private Scrollbar slowdown;
   private Scrollbar arrival;
   private Thread runner;
}

class RoadCanvas extends Canvas
{  
   public RoadCanvas()
   {  
      freeway = new Road();
      row = 0;      
   }

   public void update(double slowdown, double arrival)
   {  
      freeway.update(slowdown, arrival);
      if (buffer == null) 
      {  
         xsize = size().width;
         ysize = size().height;
         buffer = createImage(xsize, ysize);
      }
      Graphics bg = buffer.getGraphics();
      freeway.paint(bg, row, XDOTDIST, DOTSIZE);
      if (row < ysize - 2 * DOTSIZE + 1) 
         row += DOTSIZE;
      else
      {  
         bg.copyArea(0, DOTSIZE, xsize, ysize - DOTSIZE, 0, -DOTSIZE);
         bg.clearRect(0, ysize - DOTSIZE, xsize, DOTSIZE);
      }
      bg.dispose();
      repaint();
   }

   public void paint(Graphics g)
   {  
      if (buffer != null) 
         g.drawImage(buffer, 0, 0, null);  
   }

   public void update(Graphics g)
   {  
      paint(g);
   }
   
   private final int DOTSIZE = 2;   
   private final int XDOTDIST = 1;
   private Road freeway;
   private Image buffer;
   private int row;
   private int xsize;
   private int ysize;
}

class Road
{
   public Road()
   {  
      speed = new int[LENGTH];
      colors = new Color[LENGTH];
      for (int i = 0; i < LENGTH; i++) speed[i] = -1;
   }
   
   public void update(double prob_slowdown, double prob_create)
   {  
      int i = 0;
      while(i < LENGTH && speed[i] == -1) 
         i++;
      while (i < LENGTH)
      {  
         if (Math.random() <= prob_slowdown && speed[i] > 0)
            speed[i]--;
         else if (speed[i] < MAXSPEED)
            speed[i]++;
         int inext = i + 1;
         while(inext < LENGTH && speed[inext] == -1) 
            inext++;
         if (inext < LENGTH)
         {  
            if (speed[i] >= inext - i) 
               speed[i] = inext - i - 1;
         }
         if (speed[i] > 0)
         {  
            if (i + speed[i] < LENGTH)
            {
               int ni = i + speed[i];
               speed[ni] = speed[i];
               colors[ni] = colors[i];
            }
            speed[i] = -1;
         }
         i = inext;
      }
      if (Math.random() <= prob_create && speed[0] == -1)
      {
         speed[0] = (int)(5.99 * Math.random());
         colors[0] = ++count % 10 == 0 ? Color.red : Color.black;
      }
   }
   
   public void paint(Graphics g, int row, int dotdist, int dotsize)
   {  
      for (int i = 0; i < LENGTH; i++)
      {
         g.setColor(colors[i]);
         if (speed[i] >= 0) g.fillRect(i * dotdist, row, dotsize, dotsize);
      }
   }
   public static final int LENGTH = 400;
   public static final int MAXSPEED = 5;

   private int[] speed;
   private Color[] colors;
   private int count;
}
