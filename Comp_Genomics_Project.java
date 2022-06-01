import javax.swing.JOptionPane;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HBGodev {
	
	static int G,N,L,T,match,mismatch,indel;
	static Random rand = new Random();
	static String DNA;

	public static void main(String[] args) throws IOException{
		
		String[] options= {"Kendim DNA Dizilimi Gireceðim","Rastgele DNA Dizilimi"};
		String[][] OverlapMatris = new String[N][N];
		
		int secim1 = JOptionPane.showOptionDialog(null, "Hangi Islemi Yapmak Istiyorsunuz?","Bir Tusa Basin",JOptionPane.DEFAULT_OPTION, 
			JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		
		if(secim1==0) {
			DNA= JOptionPane.showInputDialog("DNA Diziliminizi giriniz.");
			G=DNA.length();
			bilgisor();
		}
		
		else {
			bilgisor();
			DNA=String.valueOf(dnaolustur());
		}
		
		System.out.println("DNA: "+DNA+"\n");
		String[] sekans=sekansolustur();
		String[] tumleyensekans=tumleyensekansolustur(sekans);
		System.out.println("Sekanslar\n"+"--------");
		for(int i=0;i<sekans.length;i++) 
			System.out.println(sekans[i]);
		System.out.println("\nSekanslarin Tumleyenleri\n"+"--------");
		for(int i=0;i<tumleyensekans.length;i++) 
			System.out.println(tumleyensekans[i]);
		OverlapMatris=skorlama(sekans,tumleyensekans);
		dosyayaYaz(OverlapMatris,sekans,tumleyensekans,DNA);
		layout(OverlapMatris,sekans,tumleyensekans);
		JOptionPane.showMessageDialog(null,"Sonuclar Dosyaya Kaydedildi!");
		
	}
	
	public static void bilgisor() {
		
		if(G==0)	//DNA dizilimi kullanici tarafindan girildiginde bu alanin sorulmasini engelleyen kosul.
			G= Integer.parseInt(JOptionPane.showInputDialog("Istediðiniz DNA uzunlugunu giriniz."));
		
		N= Integer.parseInt(JOptionPane.showInputDialog("Istediðiniz sekans adedini giriniz."));
		L= Integer.parseInt(JOptionPane.showInputDialog("Her bir sekansýn uzunlugunu giriniz."));
		T= Integer.parseInt(JOptionPane.showInputDialog("Skor Matrisinde kabul edilebilecek olan en küçük (T) skorunu giriniz."));
		
		while(true) {
		match= Integer.parseInt(JOptionPane.showInputDialog("Pozitif bir match odulu giriniz."));
		
		if(match>0)
			break;
		else;
		}
		
		while(true) {
			mismatch= Integer.parseInt(JOptionPane.showInputDialog("Pozitif bir mismatch cezasi giriniz."));
			if(mismatch>0)
				break;
			else;
			}
		
		while(true) {
			indel= Integer.parseInt(JOptionPane.showInputDialog("Pozitif bir indel cezasi giriniz."));
			if(indel>0)
				break;
			else;
			}
	}
	
	public static char[] dnaolustur() {
		
		char[] rastgele_DNA= new char[G];
		
		for(int i=0;i<G;i++) {
			
			int a = rand.nextInt(4);
			
			switch(a){
			
			case 0:
				rastgele_DNA[i]='A';
				break;
			case 1:
				rastgele_DNA[i]='C';
				break;
			case 2:
				rastgele_DNA[i]='G';
				break;
			case 3:
				rastgele_DNA[i]='T';
				break;
			}
		}
		return rastgele_DNA;
	}
	
	public static String[] sekansolustur() {
		
		ArrayList<Integer> list = new ArrayList<>();
		String[] Sekans= new String[N];
		int j=0;
		
		for(int i=0,k=0;i<N;i++) {
			j=rand.nextInt(DNA.length()-L)+1;
			
			//Daha onceden random bulunan sayinin tekrar bulunmasini engelleyen yapi.
			if(list.contains(j))
				i--;
			
			else {
				list.add(j);
				Sekans[k++]=DNA.substring(j, j+L);
			}
		}
		
		return Sekans;
	}

	public static String[] tumleyensekansolustur(String[] Sekans) {

		StringBuilder str = new StringBuilder();
		String[] TumleyenSekans= new String[N];
		
		for(int k=0;k<N;k++) {
			for(int i=0;i<L;i++) {
				
				switch(Sekans[k].charAt(i)){
				case 'A':
					str.append('T');
					break;
				case 'T':
					str.append('A');
					break;
				case 'G':
					str.append('C');
					break;
				case 'C':
					str.append('G');
					break;
				default:
					break;
				}
			}
			
			str.reverse();
			TumleyenSekans[k]=str.toString();
			str.delete(0, L); //Bir onceki sekansdan kalan degerleri siler.
		}
		return TumleyenSekans;
	}

	public static String[][] skorlama(String[] sekans,String[] tumleyen) {
		
		String[][] OverlapMatris = new String[N][N];
		int[] siralisatir = new int[L-1];
		int[] siralisutun = new int[L-1];
		int[][] Matris = new int[L][L];
		int s;
			
		//Matrisin ust ucgenindeki skorlamalarýný yapan donguler(k<l icin rk,rl karsilastirmasi). 
			for(int m=0;m<N;m++) {
				for(int t=m+1;t<N;t++) {
					//Skorlama islemi.
					for(int i=1;i<L;i++) {
						for(int j=1;j<L;j++) {
							Matris[i][0]=0;
							Matris[0][j]=0;
							s=( sekans[m].charAt(j)==sekans[t].charAt(i) )?match:0-mismatch;
							Matris[i][j]=Math.max(Matris[i-1][j]-indel,Math.max(Matris[i][j-1]-indel,Matris[i-1][j-1]+s));
						}
					}
				
				//Best Overlap icin en sagdaki sutun ve en asagidaki satiri buyukten kucuge siralayan döngüler. 
					for(int i=L-1;i<L;i++) {
						for(int j=1;j<L;j++) {
						siralisatir[j-1]=Matris[i][j];
						}	
					}
					for(int i=1;i<L;i++) {
						for(int j=L-1;j<L;j++) {
						siralisutun[i-1]=Matris[i][j];
						}
					}
					Arrays.sort(siralisatir);
					Arrays.sort(siralisutun);
					
					OverlapMatris[m][t]=(Math.max(siralisatir[siralisatir.length-1], siralisutun[siralisutun.length-1])>=T)? Integer.toString(Math.max(siralisatir[siralisatir.length-1], siralisutun[siralisutun.length-1])):" ";
				}
			}
			
		//Matrisin alt ucgenindeki skorlamalarýný yapan donguler(k>l icin rk,rl komplement karsilastirmasi). 
			for(int m=0;m<N;m++) {
				for(int t=0;t<m+1;t++) {
					for(int i=1;i<L;i++) {
						for(int j=1;j<L;j++) {
							Matris[i][0]=0;
							Matris[0][j]=0;
							s=( sekans[m].charAt(j)==tumleyen[t].charAt(i) )?match:0-mismatch;
							Matris[i][j]=Math.max(Matris[i-1][j]-indel,Math.max(Matris[i][j-1]-indel,Matris[i-1][j-1]+s));
						}
					}
				
					for(int i=L-1;i<L;i++) {
						for(int j=1;j<L;j++) {
						siralisatir[j-1]=Matris[i][j];
						}	
					}
					for(int i=1;i<L;i++) {
						for(int j=L-1;j<L;j++) {
						siralisutun[i-1]=Matris[i][j];
						}
					}
					Arrays.sort(siralisatir);
					Arrays.sort(siralisutun);
							
					OverlapMatris[m][t]=(Math.max(siralisatir[siralisatir.length-1], siralisutun[siralisutun.length-1])>=T)? Integer.toString(Math.max(siralisatir[siralisatir.length-1], siralisutun[siralisutun.length-1])):" ";	
				}
			}
			
			for(int i=0;i<N;i++) {
				OverlapMatris[i][i]="*";
			}
			for(int i=0;i<N;i++) {
				for(int j=0;j<N;j++)
					System.out.print("\t"+OverlapMatris[i][j]);
			System.out.print("\n\n");
			}
			
			return OverlapMatris;
	}

	public static void layout(String[][] OverlapMatris,String[] sekans1,String[] tumleyensekans) {
		
		int[][]Overlap=new int[N][N];
		String[] sekans= sekans1;
		String[] tumleyen= tumleyensekans;
		
		//String tipli OverlapMatrisini int turunden matrise donusturme islemi
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				switch(OverlapMatris[i][j]) {
				case "*":
					OverlapMatris[i][j]="-2";
					break;
				case " ":
					OverlapMatris[i][j]="0";
					break;
				default:
				}
				Overlap[i][j]=Integer.parseInt(OverlapMatris[i][j]);
			}
		}
		//Karsilastiracak sekans kalmayana kadar calisan dongu
		while(true) {
			
			ArrayList<String> list = new ArrayList<>();
			int max2=-1,max=-1;
			int imax=-1,jmax=-1,kmax=-1,j,s,i;
		
				//Overlap Matrisindeki en buyuk skoru bulan dongu
				for(i=0;i<N;i++) {
					for(j=0;j<N;j++) {
						if(Overlap[i][j]>max) {
							max=Overlap[i][j];
							imax=i;
							jmax=j;
						}
					}
				}
				
				if(Overlap[imax][jmax]>=T) {
						
							list.add(sekans[imax]+" "+imax);list.add(sekans[jmax]+" "+jmax);
							Overlap[imax][jmax]=-1;
							
							//Ortusen sekanslari yukari yonlu buyuten kisim
							while(true) {
								
								for(int k=0;k<N;k++) {
									if(Overlap[imax][k]>max2) {
										max2=Overlap[imax][k];
										kmax=k;
									}
								}
								
								if(imax<kmax && Overlap[imax][kmax]>=T) {
									list.add(0,sekans[kmax]+" "+kmax);
									Overlap[imax][kmax]=-1;
									s=kmax; imax=s;
									
								}
								
								else if(imax>kmax && Overlap[imax][kmax]>=T) {
									list.add(0,tumleyen[kmax]+" "+kmax+"*");
									break;
								}
								else if (Overlap[imax][kmax]<T)
									break;
								
								max2=-1;
							}
							
							//Ortusen sekanslari asagi yonlu buyuten kisim
							while(true) {
								
								for(int k=0;k<N;k++) {
									if(Overlap[jmax][k]>max2) {
										max2=Overlap[jmax][k];
										kmax=k;
									}
									
								}
								if(jmax<kmax && Overlap[jmax][kmax]>=T) {
									list.add(sekans[kmax]+" "+kmax);
									Overlap[jmax][kmax]=-1;
									s=kmax; jmax=s;
									
								}
								
								else if(jmax>kmax && Overlap[jmax][kmax]>=T) {
									list.add(tumleyen[kmax]+" "+kmax+"*");
									break;
								}
								else if (Overlap[jmax][kmax]<T)
									break;
								
								max2=-1;	
							}
						
				for(int a=0;a<list.size();a++)
					System.out.println(list.get(a));
				System.out.println();	
			}
			
			//Karsilastiracak sekans kalmayýnca sonsuz donguyu sonlandýran kosul.
			else
				break;
				
		}
	}

	public static void dosyayaYaz(String[][] OverlapMatris,String[] sekans,String[] tumleyensekans,String dna) throws IOException {
		
		PrintWriter yazdir = new PrintWriter("HBGodev.txt");
		
		yazdir.println("DNA: "+dna);
		
		yazdir.println("\n---- Sekanslar ----\n");
		for(int i=0;i<sekans.length;i++) 
			yazdir.println(sekans[i]);
		
		yazdir.println("\n---- Sekanslarin Tumleyenleri ----\n");
		for(int i=0;i<tumleyensekans.length;i++) 
			yazdir.println(tumleyensekans[i]);
		
		yazdir.println("\n---- Overlap Skor Matrisi ----\n");
		yazdir.print("    ");
		
		for(int i=0;i<N;i++) {
			yazdir.printf("%4d",i);
		}
		yazdir.printf("\n");
		
		for(int i=0;i<N;i++) {
			yazdir.printf("\n");
			yazdir.printf("%4d",i);
			
			for(int j=0;j<N;j++) {
			yazdir.printf("%4s",OverlapMatris[i][j]);
			}
			yazdir.printf("\n");
		}
		yazdir.close();
	}

}
