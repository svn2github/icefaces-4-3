/*
 * Copyright 2004-2014 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.samples.showcase.example.ace.dashboard;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AjaxBehaviorEvent;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.*;

@ManagedBean(name= DashboardDynamicPanesBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DashboardDynamicPanesBean implements Serializable {
    public static final String BEAN_NAME = "dashboardDynamicPanesBean";
	public String getBeanName() { return BEAN_NAME; }

	public static List<String> quotes = new ArrayList(100);
	private Random random = new Random(System.currentTimeMillis());

	static {
		quotes.add("Twenty years from now you will be more disappointed by the things that you didn't do than by the ones you did do. So throw off the bowlines. Sail away from the safe harbor. Catch the trade winds in your sails. Explore. Dream. Discover.;Mark Twain");
		quotes.add("I can't understand why people are frightened of new ideas. I'm frightened of the old ones.;John Cage");
		quotes.add("The tragedy of life is not so much what men suffer, but rather what they miss.;Thomas Carlyle");
		quotes.add("The greatest mistake you can make in life is to be continually fearing you will make one.;Elbert Hubbard");
		quotes.add("Anyone who has never made a mistake has never tried anything new.;Albert Einstein");
		quotes.add("It's better to hang out with people better than you. Pick out associates whose behavior is better than yours and you'll drift in that direction.;Warren Buffett");
		quotes.add("The question is not who is going to let me, it's who is going to stop me.;Ayn Rand");
		quotes.add("The dumbest people I know are those who know it all.;Malcolm Forbes");
		quotes.add("If what you’re doing is not your passion, you have nothing to lose.;George Eliot");
		quotes.add("The person who says something is impossible should not interrupt the person who is doing it.;Unknown");
		quotes.add("Making the simple complicated is commonplace. Making the complicated simple, awesomely simple, that's creativity.;Charles Mingus");
		quotes.add("All our dreams can come true – if we have the courage to pursue them.;Walt Disney");
		quotes.add("The best years of your life are the ones in which you decide your problems are your own. You do not blame them on your mother, the ecology, or the president. You realize that you control your own destiny.;Albert Ellis");
		quotes.add("If opportunity doesn’t knock, build a door.;Milton Berle");
		quotes.add("Let us so live that when we come to die even the undertaker will be sorry.;Mark Twain");
		quotes.add("A good traveler has no fixed plans, and is not intent on arriving.;Lao Tzu");
		quotes.add("The World is a book, and those who do not travel read only a page.;Saint Augustine");
		quotes.add("Once in a while it really hits people that they don't have to experience the world in the way they have been told to.;Alan Keightley");
		quotes.add("The important thing is not being afraid to take a chance. Remember, the greatest failure is to not try. Once you find something you love to do, be the best at doing it.;Debbi Fields");
		quotes.add("Be who you are and say what you feel, because those who mind don't matter, and those who matter don't mind.;Dr. Seuss");
		quotes.add("Yesterday is history. Tomorrow is a mystery. Today is a gift. That's why we call it 'The Present'.;Eleanor Roosevelt");
		quotes.add("Whenever you find yourself on the side of the majority, it is time to pause and reflect.;Mark Twain");
		quotes.add("Money alone isn't enough to bring happiness . . . happiness [is] when you're actually truly ok with losing everything you have.;Tony Hsieh");
		quotes.add("To live is the rarest thing in the world. Most people exist, that’s all.;Oscar Wilde");
		quotes.add("Don't wait. The time will never be just right.;Napoleon Hill");
		quotes.add("Live as if you were to die tomorrow. Learn as if you were to live forever.;Mahatma Gandhi");
		quotes.add("A year from now you may wish you had started today.;Karen Lamb");
		quotes.add("Live simply, so others may simply live.;Mahatma Gandhi");
		quotes.add("Perfection is achieved, not when there is nothing more to add, but when there is nothing left to take away.;Antoine de Saint-Exupery");
		quotes.add("We act as though comfort and luxury were the chief requirements of life, when all that we need to make us happy is something to be enthusiastic about.;Albert Einstein");
		quotes.add("The key to performance is elegance, not battalions of special cases.;Jon Bentley and Douglas McIlroy");
		quotes.add("A desk is a dangerous place from which to view the world.;John Le Carre");
		quotes.add("One machine can do the work of fifty ordinary men. No machine can do the work of one extraordinary man.;Elbert Hubbard");
		quotes.add("Folks who never do any more than they are paid for, never get paid more than they do.;Elbert Hubbard");
		quotes.add("The trouble in corporate America is that too many people with too much power live in a box (their home), then travel the same road every day to another box (their office).;Faith Popcorn");
		quotes.add("We buy things we don't need with money we don't have to impress people we don't like.;David Ramsey");
		quotes.add("It is hard to fail, but it is worse never to have tried to succeed.;Theodore Roosevelt");
		quotes.add("Instead of wondering when your next vacation is, maybe you should set up a life you don't need to escape from.;Seth Godin");
		quotes.add("It’s a funny thing about life. If you refuse to accept anything but the best, you very often get it.;W. Somerset Maugham");
		quotes.add("We make a fine bourbon, at a profit if we can, at a loss if we must, but always FINE BOURBON.;Pappy Van Winkle");
		quotes.add("An army of lions led by a sheep is no match for an army of sheep led by a lion.;Winston Churchill");
		quotes.add("A ship in port is safe, but that is not what ships are for. ;Grace Hopper");
		quotes.add("I never let my schooling get in the way of my education;Mark Twain");
		quotes.add("There are no traffic jams along the extra mile.;Roger Staubach");
		quotes.add("The world is a dangerous place, not because of those who do evil, but because of those who look on and do nothing.;Albert Einstein");
		quotes.add("Whatever you think you can do or believe you can do, begin it. Action has magic, grace and power in it.;Johann Wolfgang von Goethe");
		quotes.add("Defeat is not the worst of failures.  Not to have tried is the true failure.;George Woodberry");
		quotes.add("It is always the start that requires the greatest effort.;James Cash Penney ");
		quotes.add("The best thinking has been done in solitude.;Thomas Edison");
		quotes.add("If you will live like no one else, later you can live like no one else.;Dave Ramsey");
		quotes.add("The harder I work, the luckier I get.;Samuel Goldwyn");
		quotes.add("Life is not measured by the number of breaths we take, but by the moments that take our breath away.;Maya Angelou");
		quotes.add("Never regret. If it's good, it's wonderful. If it's bad, it's experience.;Victoria Holt");
		quotes.add("We are what we repeatedly do. Excellence, then is not an act but a habit.;Aristotle");
		quotes.add("Happiness is not something you postpone for the future. It is something you design for the present.;Jim Rohn");
		quotes.add("Don’t make a decision based solely on popularity. Just because other people are doing it doesn’t mean it’s the best choice for you.;Unknown");
		quotes.add("Sometimes you need to be alone to reflect on life.  Take time out to take care of yourself.  You deserve it.;Unknown");
		quotes.add("One travels more usefully when alone, because he reflects more.;Thomas Jefferson");
		quotes.add("We must all suffer one of two things: the pain of discipline or the pain of regret and disappointment.;Jim Rohn");
		quotes.add("I haven't failed, I've just found 10,000 ways that don't work.;Thomas Alva Edison");
		quotes.add("The three most harmful addictions are heroin, carbohydrates and a monthly salary.;Nassim Nicholas Taleb");
		quotes.add("Never half-ass two things, whole-ass one thing.;Ron Swanson");
		quotes.add("Humility is not thinking less of yourself, it's thinking of yourself less.;C. S. Lewis");
		quotes.add("Every traveler has a home of his own, and he learns to appreciate it the more from his wandering.;Charles Dickens");
		quotes.add("Study while others are sleeping. Work while others are loafing. Prepare while others are playing. And dream while others are wishing.;William A. Ward");
		quotes.add("Nothing diminishes anxiety faster than action.;Walter Anderson");
		quotes.add("If you don't design your own life plan, chances are you'll fall into someone else's plan.;Jim Rohn");
		quotes.add("If you can solve your problem, then what is the need of worrying? If you cannot solve it, then what is the use of worrying?;Shantideva");
		quotes.add("Sometimes life is going to hit you in the head with a brick. Don't lose faith.;Steve Jobs");
		quotes.add("It takes as much energy to wish as it does to plan.;Eleanor Roosevelt");
		quotes.add("I am so clever that sometimes I don't understand a single word of what I am saying.;Oscar Wilde");
		quotes.add("Underpromise. Overdeliver.;Tom Peters");
		quotes.add("The world needs dreamers and the world needs doers. But above all, the world needs dreamers who do.;Sarah Ban Breathnach");
		quotes.add("When you listen, it's amazing what you can learn. When you act on what you've learned, it's amazing what you can change.;A. McLaughlin");
		quotes.add("Simplicity is the ultimate sophistication.;Leonardo Da Vinci");
		quotes.add("They may forget what you said, but they will never forget how you made them feel.;Carl W. Buechner");
		quotes.add("Be too busy to have time for regrets.;Angela Wright");
		quotes.add("Don't spend time with anyone you don't like.;Prue Leith");
		quotes.add("Work hard but make sure you enjoy life too.;Ted Smart");
		quotes.add("You only get one crack at a big opportunity so make sure you recognize it and grab it with both hands.;Darren Richards");
		quotes.add("The more reasons you have for achieving your goal, the more determined you will become.;Brian Tracy");
		quotes.add("Nothing is really work unless you would rather be doing something else.;James M. Barrie");
		quotes.add("The highest reward for a person's toil is not what they get for it, but what they become by it.;John Ruskin");
		quotes.add("Pleasure in the job puts perfection in the work.;Aristotle");
		quotes.add("Long-range goals keep you from being frustrated by short-term failures.;J. C. Penney");
		quotes.add("Respond. Don't react. Listen. Don't talk. Think. Don't assume.;Raji Lukkoor");
		quotes.add("Do not fear going forward slowly. Fear only to stand still.;Chinese Proverb");
		quotes.add("Win without boasting. Lose without excuse.;Albert Payson");
		quotes.add("Talent is cheaper than table salt. What separates the talented individual from the successful one is a lot of hard work.;Stephen King");
		quotes.add("Don't re-open old wounds in order to examine their origins. Leave them healed.;Richard Bandler");
		quotes.add("The act of taking the first step is what separates the winners from the losers.;Brian Tracy");
		quotes.add("A goal should scare you a little, and excite you a lot.;Dr. Joe Vitale");
		quotes.add("A person who won't read has no advantage over one who can't read.;Mark Twain");
		quotes.add("It always seems impossible until it's done.;Nelson Mandela");
		quotes.add("There is only one success - to be able to spend your life in your own way.;Christopher Morley");
		quotes.add("Always and never are two words you should always remember never to use.;Wendell Johnson");
		quotes.add("Don't find fault, find a remedy.;Henry Ford");
		quotes.add("A man is a success if he gets up in the morning and goes to bed at night and in between does what he wants to do.;Bob Dylan");
		quotes.add("A superior man is modest in his speech, but exceeds in his actions.;Confucious");
		quotes.add("You cannot do a kindness too soon, for you never know how soon it will be too late.;Ralph Waldo Emerson");
	}

	public DashboardDynamicPanesBean() {
		
	}

	@PostConstruct
	public void init() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		Object o = sessionMap.get("DashboardDynamicPanesBean.state");
		if (o == null) {
			List<DashboardPaneState> states = new ArrayList<DashboardPaneState>(10);
			for (int i = 0; i < 5; i++) {
				states.add(getRandomQuote());
			}
			this.states = states;
		} else {
			this.states = (List<DashboardPaneState>) o;
		}
	}

	private DashboardPaneState getRandomQuote() {
		int randomInt = random.nextInt(100);
		String item = quotes.get(randomInt);
		int semicolonIndex = item.indexOf(";");
		String quote = item.substring(0, semicolonIndex);
		String author = item.substring(semicolonIndex + 1);
		return new DashboardPaneState(0, 0, 0, 0, false, quote, author);
	}

	public void addRandomQuote() {
		this.states.add(getRandomQuote());
	}

	private List<DashboardPaneState> states;
	public List<DashboardPaneState> getStates() { return states; }
	public void setStates(List<DashboardPaneState> states) { this.states = states; }

	public static class DashboardPaneState implements Serializable {

		public DashboardPaneState(int row, int column, int sizeY, int sizeX, boolean closed,
				String quote, String author) {
			this.row = row;
			this.column = column;
			this.sizeY = sizeY;
			this.sizeX = sizeX;
			this.closed = closed;
			this.quote = quote;
			this.author = author;
		}

		private int row;
		public int getRow() { return row; }
		public void setRow(int row) { this.row = row; }

		private int column;
		public int getColumn() { return column; }
		public void setColumn(int column) { this.column = column; }

		private int sizeY;
		public int getSizeY() { return sizeY; }
		public void setSizeY(int sizeY) { this.sizeY = sizeY; }

		private int sizeX;
		public int getSizeX() { return sizeX; }
		public void setSizeX(int sizeX) { this.sizeX = sizeX; }

		private boolean closed;
		public boolean isClosed() { return closed; }
		public void setClosed(boolean closed) { this.closed = closed; }

		private String quote;
		public String getQuote() { return quote; }
		public void setQuote(String quote) { this.quote = quote; }

		private String author;
		public String getAuthor() { return author; }
		public void setAuthor(String author) { this.author = author; }
	}
}