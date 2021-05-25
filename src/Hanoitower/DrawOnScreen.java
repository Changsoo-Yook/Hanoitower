package Hanoitower;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class DrawOnScreen extends Canvas {
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