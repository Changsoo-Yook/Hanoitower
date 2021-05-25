package Hanoitower;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GameMain extends JFrame implements Runnable {
	JLabel movelabel;
	DrawOnScreen draw_scr;
	Stack<Integer> stackLeft;
	Stack<Integer> stackCenter;
	Stack<Integer> stackRight; 
	Stack<Integer> stackMoveCnt; 
	Object p_obj;					// stack에서 뺀 plate를 저장할 곳
	
	int resolution_width = 1365;
	int resolution_height = 768;
	int game_order = 0;			// 게임 모드 전환용
	int sel_x;							// selector x, y좌표
	int sel_y;
	int sel_op_x=145;				// 옵션에서 selector x의 좌표
	int sel_op_y=270;
	int sel_location = 0;				// selector의 위치 저장(나중에 goal과 위치 비교)
	
	int[] goal_map = {0,1,2};		// plate가 놓여질 판때기 왼쪽, 중간, 오른쪽
	int[] plate = {0,1,2,3,4,5,6};		// plate의 갯수만큼의 배열
	
	int[] p_x;						// 각 plate의 x좌표 저장 배열
	int[] p_y;						// 각 plate의 y좌표 저장 배열
	int before_p_x;				// selector가 선택한 plate의 x좌표를 기억하기 위한 용도 (이동 실패를 위한 대비)
	int before_p_y;				// selector가 선택한 plate의 y좌표를 기억하기 위한 용도
	int p_num = 3;				// plate의 갯수, 난이도와 관련있음.
	int p_sel_num;				// selector가 선택한 plate의 번호
	int p_exist = 0;				// stack에 들어갈 plate 존재여부
	int selected_p_beforelocation = 0;		// selector가 plate를 선택했을 시점의 위치
	int isSelecting = 0;			// plate를 선택하자. 0은 비선택, 1은 선택
	int moveCnt=0;			// 이동 횟수를 저장한다.
	int playingTime = 0;	// 게임 시작부터 시간을 체크한다.
	int playSec=0;
	int goal_check;			// 게임시 완성조건을 체크한다.
	int[] goalPlate_x ={78, 507, 936};
	int changeIcon=0;			// 옵션화면에서 화살표의 이펙트 효과
	
	public GameMain() {
		movelabel = new JLabel("이동 횟수 : ");
		stackLeft = new Stack<Integer>();
		stackCenter = new Stack<Integer>();
		stackRight = new Stack<Integer>();
		stackMoveCnt = new Stack<Integer>();
		
		p_x = new int[7];		// 1:p1, 2:p2, 3:p3, 4:p4, 5:p5
		p_y = new int[7];		// 1:p1, 2:p2, 3:p3, 4:p4, 5:p5
		
		Init();

		InputProcess();		// 키보드 입력 관련
		
		draw_scr = new DrawOnScreen(this);
		
		Thread thread = new Thread(this);
		Thread time_thread = new TimerForGame(this);
		thread.start();
		time_thread.start();

		add(draw_scr);
		setTitle("재미있는 하노이탑 beta");
		setBounds(50,50,resolution_width, resolution_height);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);	
	}
	
	public void Init() {
		stackLeft.removeAllElements();
		stackCenter.removeAllElements();
		stackRight.removeAllElements();
		
		sel_location = 0;
		p_sel_num = 0;				
		p_exist = 0;				
		selected_p_beforelocation = 0;		
		isSelecting = 0;					
		playingTime = 0;	
		playSec = 0;
		changeIcon = 0;			
		
		switch(p_num){
			case 3:{	plate_is_3();	break;	}
			case 4:{	plate_is_4();	break;	}
			case 5:{	plate_is_5();	break;	}
			case 6:{	plate_is_6();	break;	}
		}
		
		moveCnt=0;
		sel_x = 78;
		sel_y = 680;
		sel_y -= (70*stackLeft.size());					//	초기 selector의 위치
	}
	
	public void run() {
		while(true) {
			draw_scr.repaint();	
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {e.printStackTrace();}
		} 
	}
	
	public void InputProcess() {
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				switch(game_order) {
					case 0:{		// 게임 타이틀
						if(e.getKeyCode() == KeyEvent.VK_SPACE)  game_order = 1;		//게임 옵션으로 이동한다.
						break;
					}
					case 1:{		// 게임 옵션
						if(e.getKeyCode() == KeyEvent.VK_LEFT)	{
							if(sel_op_x > 150) sel_op_x -= 800;
							if(sel_op_y == 545) {
									if(e.getKeyCode() == KeyEvent.VK_LEFT)	{
										sel_op_x = 145;
										sel_op_y = 270;
									}
							}
						}
						else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
							if(sel_op_x < 940) sel_op_x += 800;
							if(sel_op_y == 545) {
								if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
									sel_op_x = 945;
									sel_op_y = 270;
								}
							}
						}	
						else if(e.getKeyCode() == KeyEvent.VK_UP) {
							sel_op_x = 145;
							sel_op_y = 270;
						}
						else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
							sel_op_x = 550;
							sel_op_y = 545;
						}
						else if(e.getKeyCode() == KeyEvent.VK_SPACE)  {
							if(sel_op_x == 145) {		// 왼쪽에 있으면
								changeIcon = 1;
								if(p_num > 3) p_num--;
							}
							
							else if(sel_op_x == 945) {	// 오른쪽에 있으면
								changeIcon = 2;
								if(p_num < 6) p_num++;
							}
							
							else if(sel_op_y == 545) {		// 중앙이면
								changeIcon=3;
								Init();		// 초기화 한번 하자.
							}
						}
						break;
					}
					case 2:{		// 게임 플레이
						if(e.getKeyCode() == KeyEvent.VK_LEFT)		MoveLeft();
						if(e.getKeyCode() == KeyEvent.VK_SPACE) 	PressedSpace();
						if(e.getKeyCode() == KeyEvent.VK_RIGHT) 	MoveRight();
						break;
					}
					case 3:{ 		// 최종 결과
						if(e.getKeyCode() == KeyEvent.VK_SPACE) {
							game_order = 1;
							sel_op_x=145;		
							sel_op_y=270;
						}
						break;
					}
				}
			}
			
			public void keyReleased(KeyEvent e) {
				if(game_order==1) {
					if(e.getKeyCode() == KeyEvent.VK_SPACE)  {
						if(sel_op_x == 145 || sel_op_x == 945) changeIcon = 0;
						else if(sel_op_y == 545)  {
							changeIcon = 4;
							playingTime = 0;
							game_order = 2;
						}
					}
				}
			}
		});
	}
	
	public void plate_is_3(){
		int mix_type=(int)(Math.random() * 6);
		switch(mix_type) {
			case 0: {
				goal_check=1;		
				stackLeft.push(3);	 	p_x[3] =78; p_y[3]=610;				
				stackLeft.push(2);		p_x[2] =78; p_y[2]=540; 
				stackLeft.push(1);	 	p_x[1] =78; p_y[1]=470;
				break;
			}
			case 1: {
				goal_check=2;		
				stackLeft.push(3);	 	p_x[3] =78; p_y[3]=610;				
				stackLeft.push(2);		p_x[2] =78; p_y[2]=540; 
				stackLeft.push(1);	 	p_x[1] =78; p_y[1]=470;
				break;
			}
			case 2: {
				goal_check=0;
				stackCenter.push(3);	 	p_x[3] =507; p_y[3]=610;				
				stackCenter.push(2);		p_x[2] =507; p_y[2]=540; 
				stackCenter.push(1);	 	p_x[1] =507; p_y[1]=470; 
				break;
			}
			case 3: {
				goal_check=2;
				stackCenter.push(3);	 	p_x[3] =507; p_y[3]=610;				
				stackCenter.push(2);		p_x[2] =507; p_y[2]=540; 
				stackCenter.push(1);	 	p_x[1] =507; p_y[1]=470; 
				break;
			}
			case 4: {
				goal_check=0;
				stackRight.push(3);	 	p_x[3] =936; p_y[3]=610;				
				stackRight.push(2);		p_x[2] =936; p_y[2]=540; 
				stackRight.push(1);	 	p_x[1] =936; p_y[1]=470; 
				break;
			}
			case 5: {
				goal_check=1;
				stackRight.push(3);	 	p_x[3] =936; p_y[3]=610;				
				stackRight.push(2);		p_x[2] =936; p_y[2]=540; 
				stackRight.push(1);	 	p_x[1] =936; p_y[1]=470; 
				break;
			}
		}
	}
	
	public void plate_is_4(){
		int mix_type=(int)(Math.random() * 6);	
		switch(mix_type) {
			case 0: {
				goal_check=1;
				stackLeft.push(4);		p_x[4] =78; p_y[4]=610;
				stackLeft.push(3);		p_x[3] =78; p_y[3]=540;
				stackLeft.push(2);		p_x[2] =78; p_y[2]=470;
				stackLeft.push(1);		p_x[1] =78; p_y[1]=400;
				break;
			}
			case 1: {
				goal_check=2;
				stackLeft.push(4);		p_x[4] =78; p_y[4]=610;
				stackLeft.push(3);		p_x[3] =78; p_y[3]=540;
				stackLeft.push(2);		p_x[2] =78; p_y[2]=470;
				stackLeft.push(1);		p_x[1] =78; p_y[1]=400;
				break;
			}
			case 2: {
				goal_check=0;
				stackCenter.push(4);		p_x[4] =507; p_y[4]=610;
				stackCenter.push(3);		p_x[3] =507; p_y[3]=540;
				stackCenter.push(2);		p_x[2] =507; p_y[2]=470;
				stackCenter.push(1);		p_x[1] =507; p_y[1]=400;
				break;
			}
			case 3: {
				goal_check=2;
				stackCenter.push(4);		p_x[4] =507; p_y[4]=610;
				stackCenter.push(3);		p_x[3] =507; p_y[3]=540;
				stackCenter.push(2);		p_x[2] =507; p_y[2]=470;
				stackCenter.push(1);		p_x[1] =507; p_y[1]=400;
				break;
			}
			case 4: {
				goal_check=0;
				stackRight.push(4);		p_x[4] =936; p_y[4]=610;
				stackRight.push(3);		p_x[3] =936; p_y[3]=540;
				stackRight.push(2);		p_x[2] =936; p_y[2]=470;
				stackRight.push(1);		p_x[1] =936; p_y[1]=400;
				break;
			}
			case 5: {
				goal_check=1;
				stackRight.push(4);		p_x[4] =936; p_y[4]=610;
				stackRight.push(3);		p_x[3] =936; p_y[3]=540;
				stackRight.push(2);		p_x[2] =936; p_y[2]=470;
				stackRight.push(1);		p_x[1] =936; p_y[1]=400;
				break;
			}
		}
	}
	
	public void plate_is_5(){
		int mix_type=(int)(Math.random() * 6);
		switch(mix_type) {
			case 0: {
				goal_check=1;
				stackLeft.push(5);		p_x[5] =78; p_y[5]=610;
				stackLeft.push(4);		p_x[4] =78; p_y[4]=540;
				stackLeft.push(3);		p_x[3] =78; p_y[3]=470;
				stackLeft.push(2);		p_x[2] =78; p_y[2]=400;
				stackLeft.push(1);		p_x[1] =78; p_y[1]=330;
				break;
			}
			case 1: {
				goal_check=2;
				stackLeft.push(5);		p_x[5] =78; p_y[5]=610;
				stackLeft.push(4);		p_x[4] =78; p_y[4]=540;
				stackLeft.push(3);		p_x[3] =78; p_y[3]=470;
				stackLeft.push(2);		p_x[2] =78; p_y[2]=400;
				stackLeft.push(1);		p_x[1] =78; p_y[1]=330;
				break;
			}
			case 2: {
				goal_check=0;
				stackCenter.push(5);		p_x[5] =507; p_y[5]=610;
				stackCenter.push(4);		p_x[4] =507; p_y[4]=540;
				stackCenter.push(3);		p_x[3] =507; p_y[3]=470;
				stackCenter.push(2);		p_x[2] =507; p_y[2]=400;
				stackCenter.push(1);		p_x[1] =507; p_y[1]=330;
				break;
			}
			case 3: {
				goal_check=2;
				stackCenter.push(5);		p_x[5] =507; p_y[5]=610;
				stackCenter.push(4);		p_x[4] =507; p_y[4]=540;
				stackCenter.push(3);		p_x[3] =507; p_y[3]=470;
				stackCenter.push(2);		p_x[2] =507; p_y[2]=400;
				stackCenter.push(1);		p_x[1] =507; p_y[1]=330;
				break;
			}
			case 4: {
				goal_check=0;
				stackRight.push(5);		p_x[5] =936; p_y[5]=610;
				stackRight.push(4);		p_x[4] =936; p_y[4]=540;
				stackRight.push(3);		p_x[3] =936; p_y[3]=470;
				stackRight.push(2);		p_x[2] =936; p_y[2]=400;
				stackRight.push(1);		p_x[1] =936; p_y[1]=330;
				break;
			}
			case 5: {
				goal_check=1;
				stackRight.push(5);		p_x[5] =936; p_y[5]=610;
				stackRight.push(4);		p_x[4] =936; p_y[4]=540;
				stackRight.push(3);		p_x[3] =936; p_y[3]=470;
				stackRight.push(2);		p_x[2] =936; p_y[2]=400;
				stackRight.push(1);		p_x[1] =936; p_y[1]=330;
				break;
			}
		}
	}
	
	public void plate_is_6(){
		int mix_type=(int)(Math.random() * 6);	
		switch(mix_type) {
			case 0: {
				goal_check=1;
				stackLeft.push(6);		p_x[6] =78; p_y[6]=610;
				stackLeft.push(5);		p_x[5] =78; p_y[5]=540;
				stackLeft.push(4);		p_x[4] =78; p_y[4]=470;
				stackLeft.push(3);		p_x[3] =78; p_y[3]=400;
				stackLeft.push(2);		p_x[2] =78; p_y[2]=330;
				stackLeft.push(1);		p_x[1] =78; p_y[1]=260;
				break;
			}
			case 1: {
				goal_check=2;
				stackLeft.push(6);		p_x[6] =78; p_y[6]=610;
				stackLeft.push(5);		p_x[5] =78; p_y[5]=540;
				stackLeft.push(4);		p_x[4] =78; p_y[4]=470;
				stackLeft.push(3);		p_x[3] =78; p_y[3]=400;
				stackLeft.push(2);		p_x[2] =78; p_y[2]=330;
				stackLeft.push(1);		p_x[1] =78; p_y[1]=260;
				break;
			}
			case 2: {
				goal_check=0;
				stackCenter.push(6);		p_x[6] =507; p_y[6]=610;
				stackCenter.push(5);		p_x[5] =507; p_y[5]=540;
				stackCenter.push(4);		p_x[4] =507; p_y[4]=470;
				stackCenter.push(3);		p_x[3] =507; p_y[3]=400;
				stackCenter.push(2);		p_x[2] =507; p_y[2]=330;
				stackCenter.push(1);		p_x[1] =507; p_y[1]=260;
				break;
			}
			case 3: {
				goal_check=2;
				stackCenter.push(6);		p_x[6] =507; p_y[6]=610;
				stackCenter.push(5);		p_x[5] =507; p_y[5]=540;
				stackCenter.push(4);		p_x[4] =507; p_y[4]=470;
				stackCenter.push(3);		p_x[3] =507; p_y[3]=400;
				stackCenter.push(2);		p_x[2] =507; p_y[2]=330;
				stackCenter.push(1);		p_x[1] =507; p_y[1]=260;
				break;
			}
			case 4: {
				goal_check=0;
				stackRight.push(6);		p_x[6] =936; p_y[6]=610;
				stackRight.push(5);		p_x[5] =936; p_y[5]=540;
				stackRight.push(4);		p_x[4] =936; p_y[4]=470;
				stackRight.push(3);		p_x[3] =936; p_y[3]=400;
				stackRight.push(2);		p_x[2] =936; p_y[2]=330;
				stackRight.push(1);		p_x[1] =936; p_y[1]=260;
				break;
			}
			case 5: {
				goal_check=1;
				stackRight.push(6);		p_x[6] =936; p_y[6]=610;
				stackRight.push(5);		p_x[5] =936; p_y[5]=540;
				stackRight.push(4);		p_x[4] =936; p_y[4]=470;
				stackRight.push(3);		p_x[3] =936; p_y[3]=400;
				stackRight.push(2);		p_x[2] =936; p_y[2]=330;
				stackRight.push(1);		p_x[1] =936; p_y[1]=260;
				break;
			}
		}
	}
	
	public void MoveLeft(){
		if(sel_x  >  80) {			// 왼쪽화살표를 누르면
			sel_x -= 429;
			if(sel_location >= 0)		sel_location--;
			
			switch(sel_location) {
				case 0: {
					int p_cnt = stackLeft.size();
					if(p_cnt > 0)		sel_y = 610-((70*p_cnt)-70);		// 위치에 plate 갯수만큼 sel_y를 올린다.
					else 					sel_y = 610;
					
					if(isSelecting == 1) {
						sel_y = 610-(70*p_cnt);
						p_x[p_sel_num] = sel_x;
						p_y[p_sel_num] = sel_y;
					}
					break;
				}
				case 1: {
					int p_cnt = stackCenter.size();
					if(p_cnt > 0)		sel_y = 610-((70*p_cnt)-70);		// 위치에 plate 갯수만큼 sel_y를 올린다.
					else					sel_y = 610;
					
					if(isSelecting == 1) {
						sel_y = 610-(70*p_cnt);
						p_x[p_sel_num] = sel_x;
						p_y[p_sel_num] = sel_y;									
					}
					break;
				}
			}
		}
	}
	
	public void MoveRight(){
		if(sel_x < 930) {			// 오른쪽 화살표를 누르면
			sel_x += 429;			// selector를 이동시키기
			if(sel_location <= 2) sel_location++;

			switch(sel_location) {			// selector의 위치는 stack의 갯수에 의해 결정된다.
				case 1: {
					int p_cnt = stackCenter.size();
						if(p_cnt > 0)		sel_y = 610-((70*p_cnt)-70);		// 위치에 plate 갯수만큼 sel_y를 올린다.
						else 					sel_y = 610;
					
					if(isSelecting==1) {
						sel_y = 610-(70*p_cnt);
						p_x[p_sel_num] = sel_x;
						p_y[p_sel_num] = sel_y;
						return;
					}
				break;
				}
				case 2: {
					int p_cnt = stackRight.size();
						if(p_cnt > 0)		sel_y = 610-((70*p_cnt)-70);		// 위치에 plate 갯수만큼 sel_y를 올린다.
						else					sel_y = 610;
					
					if(isSelecting==1) {
						sel_y = 610-(70*p_cnt);
						p_x[p_sel_num] = sel_x;
						p_y[p_sel_num] = sel_y;
						return;
					}
				break;
				}
			}
		}
	}
	
	public void PressedSpace() {			
		isSelecting++;
		
		if(isSelecting == 1) {		// plate를 잡는다.
			switch(sel_location) {
				case 0:	{
					if(stackLeft.isEmpty())	{isSelecting=0; break;}
					else if(!stackLeft.isEmpty()) p_obj=stackLeft.pop(); break; }
				case 1:	{
					if(stackCenter.isEmpty()) {isSelecting=0; break;}
					else if(!stackCenter.isEmpty()) p_obj=stackCenter.pop(); break; }
				case 2:	{
					if(stackRight.isEmpty()) {isSelecting=0; break;}
					else if(!stackRight.isEmpty()) p_obj=stackRight.pop(); break; }
			}
			
			for(int i=p_num; i >= 1; i--) {
				if(sel_x == p_x[i] && sel_y == p_y[i]) {
					p_sel_num = i;							// plate 배열에 사용할꺼다. 어떤거 집는지 알아봐야지
					p_x[p_sel_num] = sel_x;
					p_y[p_sel_num] = sel_y;
					before_p_x = p_x[p_sel_num];
					stackMoveCnt.push(sel_location);		// plate를 집었을 때, stack에 위치를 넣는다.
				}
			}
			before_p_x = p_x[p_sel_num];					// plate를 옮기기 전에 집었던 위치를 저장시켜놔야지.
			before_p_y = p_y[p_sel_num];
		}

		else if(isSelecting > 1) {				// plate를 놓는다.
			isSelecting = 0;		
			if(before_p_x != p_x[p_sel_num]) moveCnt++;
			
			int lastMove = stackMoveCnt.pop();	
			
			switch(sel_location) {
				case 0:	{				
					stackLeft.push((Integer)p_obj); 
					if(stackLeft.size()>=2) {
						Object[] obj = stackLeft.toArray();
						int stackSize = (int)obj.length;
						int last = obj[stackSize-1].hashCode();			// stack의 가장 마지막 plate
						int beforelast = obj[stackSize-2].hashCode();		// stack의 마지막에서 바로 앞의 plate
						
						if(last > beforelast) {						// plate를 selector에서 놓았을 때 최상단 plate와 그 아래의 plate의 크기 비교
							p_x[p_sel_num] = before_p_x; 
							p_y[p_sel_num] = before_p_y;
							stackLeft.pop();
							moveCnt--;
							sel_y += 70;
							switch(lastMove) {
							case 1: {	stackCenter.push((Integer)p_obj); break;	}
							case 2: {	stackRight.push((Integer)p_obj); break;		}
							}
						}
					}
					break;
				}

				case 1:	{
					stackCenter.push((Integer)p_obj);
					
					if(stackCenter.size()>=2) {
						Object[] obj = stackCenter.toArray();
						int stackSize = (int)obj.length;
						int last = obj[stackSize-1].hashCode();			// stack의 가장 마지막 plate
						int beforelast = obj[stackSize-2].hashCode();		// stack의 마지막에서 바로 앞의 plate
						
						if(last > beforelast) {				// plate를 selector에서 놓았을 때 최상단 plate와 그 아래의 plate의 크기 비교
							p_x[p_sel_num] = before_p_x; 
							p_y[p_sel_num] = before_p_y;
							stackCenter.pop();
							moveCnt--;
							sel_y += 70;
							switch(lastMove) {
							case 0: {	stackLeft.push((Integer)p_obj); break;		}
							case 2: {	stackRight.push((Integer)p_obj); break;		}
							}
						}
					}
					break;
				}
				case 2:	{ 
					stackRight.push((Integer)p_obj);
					
					if(stackRight.size()>=2) {
						Object[] obj = stackRight.toArray();
						int stackSize = (int)obj.length;
						int last = obj[stackSize-1].hashCode();			// stack의 가장 마지막 plate
						int beforelast = obj[stackSize-2].hashCode();		// stack의 마지막에서 바로 앞의 plate	
					
						if(last > beforelast) {						// plate를 selector에서 놓았을 때 최상단 plate와 그 아래의 plate의 크기 비교
							p_x[p_sel_num] = before_p_x; 
							p_y[p_sel_num] = before_p_y;
							stackRight.pop();
							moveCnt--;
							sel_y += 70;
							switch(lastMove) {
							case 0: {	stackLeft.push((Integer)p_obj); break;		}
							case 1: {	stackCenter.push((Integer)p_obj); break;	}
							}
						}
					}
				break;
				}
			}
		}
			
		// 게임 승리 판단!
		switch(goal_check) {
			case 0:{ 
				if(stackLeft.size()==p_num) {	game_order=3;	}
				break; 
			}
			case 1:{ 
				if(stackCenter.size()==p_num) {	game_order=3;	} 
				break;
			}
			case 2:{ 
				if(stackRight.size()==p_num) {	game_order=3;	}
				break; 
			}
		}
	}
	
	public static void main(String args[]){
		new GameMain();
	}
}

////////////////////////////////////////////////////////////////////
class TimerForGame extends Thread {
	GameMain gm;
	public TimerForGame(GameMain gm) {
		this.gm = gm;
	}
	
	public void run() {
		while(true) {
		gm.playingTime++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}	
}
//////////////////////////////////////////////////////////////////////
class DrawOnScreen extends Canvas {
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image doubleBuff;
	Graphics buffGraphic;
	String dir = "./imgSc/";
	String[] file;
	String[] goal_file;
	
	Image title_space = tk.getImage("./imgSc/title_space.png");
	Image title_name = tk.getImage("./imgSc/title_name.png");
	Image titlebg_img = tk.getImage("./imgSc/landscape1.jpg");
	Image optionbg_img = tk.getImage("./imgSc/landscape2.jpg");
	Image playbg_img = tk.getImage("./imgSc/landscape3.jpg");
	Image result_bg = tk.getImage("./imgSc/landscape4.jpg");
	
	Image goal_left = tk.getImage("./imgSc/goal_plate1.png");
	Image goal_center = tk.getImage("./imgSc/goal_plate2.png");
	Image goal_right = tk.getImage("./imgSc/goal_plate3.png");

	Image select_img = tk.getImage("./imgSc/select.png");
	Image select_option_img = tk.getImage("./imgSc/select_option.png");
	Image icon = tk.getImage("./imgSc/icon.png");
	
	Image num_3 = tk.getImage("./imgSc/num_3.png");
	Image num_4 = tk.getImage("./imgSc/num_4.png");
	Image num_5 = tk.getImage("./imgSc/num_5.png");
	Image num_6 = tk.getImage("./imgSc/num_6.png");
	
	Image leftarrow_1 = tk.getImage("./imgSc/leftarrow_1.png");
	Image leftarrow_2 = tk.getImage("./imgSc/leftarrow_2.png");
	Image rightarrow_1 = tk.getImage("./imgSc/rightarrow_1.png");
	Image rightarrow_2 = tk.getImage("./imgSc/rightarrow_2.png");
	Image gamestart_bt_pressed = tk.getImage("./imgSc/gamestart_bt_pressed.png");
	Image gamestart_bt_released = tk.getImage("./imgSc/gamestart_bt_released.png");
	
	GameMain gm;
	
	DrawOnScreen(GameMain gm) {
		this.gm = gm;
		
		file = new String[6];
		file[0] = "1_plate.png";
		file[1] = "2_plate.png";
		file[2] = "3_plate.png";
		file[3] = "4_plate.png";
		file[4] = "5_plate.png";
		file[5] = "6_plate.png";
	}
	
	public void paint(Graphics g) {
		doubleBuff = createImage(gm.resolution_width, gm.resolution_height);
		buffGraphic = doubleBuff.getGraphics();
		
		update(g);
	}

	public void update(Graphics g) {
		onDisplay();
		g.drawImage(doubleBuff,0,0,this);
	}
	
	public void onDisplay() {
		switch(gm.game_order) {
		case 0: { GameTitle_Img(); break; }
		case 1: { GameOption_Img(); break; }
		case 2: { GamePlay_Img(); break; }
		case 3: { GameResult_Img(); break; }
		}
	}
	
	public void GameTitle_Img() {
		buffGraphic.clearRect(0, 0, getWidth(), getHeight());
		buffGraphic.drawImage(titlebg_img, 0, 0, this);
		buffGraphic.drawImage(title_name, 170, 100, this);
		buffGraphic.drawImage(title_space, 170, 400, this);
	}
	
	public void GameOption_Img() {
		buffGraphic.clearRect(0, 0, getWidth(), getHeight());
		buffGraphic.drawImage(optionbg_img, 0, 0, this);
		buffGraphic.setFont(new Font("HYDNKB", Font.BOLD, 70));
		buffGraphic.drawString("몇개의 원판을 원하시나요? ", 210, 150);
		buffGraphic.drawImage(select_option_img, gm.sel_op_x, gm.sel_op_y, this);
		buffGraphic.drawImage(gamestart_bt_pressed, 450, 550, this);
		
		switch(gm.changeIcon){
			case 0: {		// 버튼 누름 비활성
				buffGraphic.drawImage(leftarrow_1, 150, 250, this);
				buffGraphic.drawImage(rightarrow_1, 950, 250, this);
				break;
			}
			case 1: {		// 왼쪽 버튼 누름 활성
				buffGraphic.drawImage(leftarrow_2, 150, 250, this);
				buffGraphic.drawImage(rightarrow_1, 950, 250, this);
				break;
			}
			case 2: {		// 오른쪽 버튼 누름 활성
				buffGraphic.drawImage(leftarrow_1, 150, 250, this);
				buffGraphic.drawImage(rightarrow_2, 950, 250, this);
				break;
			}
			case 3: {		// 시작 버튼 누른 상태
				buffGraphic.drawImage(leftarrow_1, 150, 250, this);
				buffGraphic.drawImage(rightarrow_1, 950, 250, this);
				buffGraphic.drawImage(gamestart_bt_released, 450, 550, this);
				break;
			}
			
			case 4: {		// 시작 버튼 대기 상태
				buffGraphic.drawImage(leftarrow_1, 150, 250, this);
				buffGraphic.drawImage(rightarrow_1, 950, 250, this);
				buffGraphic.drawImage(gamestart_bt_pressed, 450, 550, this);
				break;
			}
		}
		switch(gm.p_num) {		// 옵션에서 plate 갯수를 정할 때 나타나는 숫자 이미지
		case 3: {
			buffGraphic.drawImage(num_3, 550, 250, this); 
			break;
		}
		case 4: {
			buffGraphic.drawImage(num_4, 550, 250, this); 
			break;
		}
		case 5: {
			buffGraphic.drawImage(num_5, 550, 250, this); 
			break;
		}
		case 6: {
			buffGraphic.drawImage(num_6, 550, 250, this); 
			break;
		}
		}
	}
	
	public void GamePlay_Img() {
		switch(gm.goal_check) {				// 목표점 랜덤으로 돌리기
			case 2: { gm.goal_map[0] = 78; gm.goal_map[1] = 507; gm.goal_map[2] = 936; break;}
			case 0: { gm.goal_map[0] = 507; gm.goal_map[1] = 936; gm.goal_map[2] = 78; break;}
			case 1: { gm.goal_map[0] = 936; gm.goal_map[1] = 78; gm.goal_map[2] = 507; break;}
		}
		
		buffGraphic.clearRect(0, 0, getWidth(), getHeight());
		buffGraphic.drawImage(playbg_img, 0, 0, this);
		
		buffGraphic.setFont(new Font("HYDNKB", Font.BOLD, 50));
		buffGraphic.drawString("이동 횟수 : "+gm.moveCnt, 50, 70);
		buffGraphic.drawString("경과 시간 : "+gm.playingTime, 600, 70);
		
		buffGraphic.drawImage(goal_left, gm.goal_map[0], 680, this);
		buffGraphic.drawImage(goal_center, gm.goal_map[1], 680, this);
		buffGraphic.drawImage(goal_right, gm.goal_map[2], 680, this);
		buffGraphic.drawImage(select_img, gm.sel_x-25, gm.sel_y, this);
		
		// 게임 모드에 따라 이미지 뿌리기
		Image plate_1 = tk.getImage(dir+file[0]);
		Image plate_2 = tk.getImage(dir+file[1]);
		Image plate_3 = tk.getImage(dir+file[2]);
		buffGraphic.drawImage(plate_1, gm.p_x[1]+100, gm.p_y[1], this);		// x=78+100, y=330
		buffGraphic.drawImage(plate_2, gm.p_x[2]+75, gm.p_y[2], this);				// x=78+75, y=400
		buffGraphic.drawImage(plate_3, gm.p_x[3]+50, gm.p_y[3], this);				// x=78+50, y=470
		
		if(gm.p_num == 4 || gm.p_num == 5 || gm.p_num == 6) {
			Image plate_4 = tk.getImage(dir+file[3]);
			buffGraphic.drawImage(plate_4, gm.p_x[4]+25, gm.p_y[4], this);				// x=78+50, y=470
		}
		if(gm.p_num == 5 || gm.p_num == 6) {
			Image plate_5 = tk.getImage(dir+file[4]);
			buffGraphic.drawImage(plate_5, gm.p_x[5], gm.p_y[5], this);				// x=78+50, y=470
		}
		if(gm.p_num == 6) {
			Image plate_6 = tk.getImage(dir+file[5]);
			buffGraphic.drawImage(plate_6, gm.p_x[6]-25, gm.p_y[6], this);				// x=78+50, y=470
		}
	}
	
	public void GameResult_Img() {
		buffGraphic.drawImage(result_bg, 0, 0, this);
	}
}