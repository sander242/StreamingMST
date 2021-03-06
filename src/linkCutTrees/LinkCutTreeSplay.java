package linkCutTrees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Link/Cut Tree with auxiliary splay trees. The methods are implemented heavily based on the Wikipedia articles on Link/Cut Trees and Splay Trees (26 January 2016).
public class LinkCutTreeSplay implements LinkCutTree {
	
	private Set<Vertex> vertices = new HashSet<Vertex>();

	@Override
	// Creates a new tree into the forest, with v as its only vertex.
	public void makeTree(Vertex v) {
		
		// Make the tree by adding the vertex
		vertices.add(v);
	
	}

	@Override
	// If v1 is a tree root and v2 is a vertex in another tree, links the trees containing v1 and v2 by adding the edge (v1, v2), making v2 the parent of v1.
	public void link(Vertex v1, Vertex v2) {
		
		// Access v1. As v1 is the root, accessing it will mean that it will have no left child in the auxiliary tree.
		access(v1);
		
		// Access v2
		access(v2);
		
		// Make v2 the left child of v1, making it the parent of v1 in the represented tree.
		v1.left = v2;
		v2.parent = v1;
		v2.pathParent = null;
	
	}

	@Override
	// Cut the represented tree at node v.
	public void cut(Vertex v) {
		// First, access v, which puts all the elements lower than v in the represented tree as the right child of v in the auxiliary tree.
		access(v);
		
		// All nodes in the left subtree of v are nodes higher than v in the represented tree. We can, therefore, disconnect the left child of v, making v the root of a represented tree.
		if (v.left != null) {
			v.left.pathParent = null;
			v.left.parent = null;
			v.left = null;
		}
		
	}

	@Override
	// Finds the root vertex of the tree vertex v exists in.
	public Vertex findRoot(Vertex v) {

		// Execute an access on vertex v to put it on the preferred path. It is then on the same preferred path and the same auxiliary tree as the represented tree root R.
		access(v);
		
		// Since auxiliary trees are keyed by depth, the root R will be the leftmost node of the auxiliary tree. Choose the left child of v recursively until we can go no further; this is the root R.
		while (v.left != null) {
			v = v.left;
		}
		
		// The root may be linearly deep. To make the next access quick, splay it.
		splay(v);
		
		// Return the root found.
		return v;
		
	}

	@Override
	public List<Vertex> findPath(Vertex v) {
		
		// Initialize the path as a list of vertices.
		List<Vertex> path = new ArrayList<Vertex>();
		
		// Access vertex v to find the auxiliary tree with all the nodes on the path from the represented tree root R to vertex v.
		access(v);	
		traversePath(v, path);		
		return path;
		
	}
	
	public void traversePath(Vertex v, List<Vertex> path) {
		if(v.right != null) {
			traversePath(v.right, path);
		}
		path.add(v);
		if(v.left != null) {
			traversePath(v.left, path);
		}
	}
	
	// Accesses the vertex v. Vertex v will no longer have any preferred children, and will be at the end of the path.
	private void access(Vertex v) {
		
		// Splay at v, bringing it to the root of the auxiliary tree.
		splay(v);
		
		// Disconnect the right subtree of v (every node that came below it on the previous preferred path).
		if (v.right != null) {
			// The root of the disconnected tree will have a path-parent pointer, which we point to v.
			v.right.pathParent = v;
			v.right.parent = null;
			v.right = null;
		}
		
		// Walk up to the represented tree root R, breaking and resetting the preferred path where necessary.
		Vertex current = v;
		// If the path that v is on, already contains the root R, the path-parent pointer will be null, and we are done with access.
		while (current.pathParent != null) {
			
			// Follow the path-parent pointer from the current node to some node on another path w.
			Vertex w = current.pathParent;
			
			// Break the old preferred path of w and reconnect it to the path v is on.
			
			// Splay at w
			splay(w);
			
			// Disconnect w's right subtree, setting its path-parent pointer to w.
			if (w.right != null) {
				w.right.pathParent = w;
				w.right.parent = null;
			}
			
			// Since all nodes are keyed by depth, and every node in the path of v is deeper than every node in the path of w, connect the tree of v as the right child of w.
			w.right = current;
			current.parent = w;
			current.pathParent = null;
			
			// Repeat the process, moving upward.
			current = w;
			
		}
		
		// Splay at v again, rotating it to root.
		splay(v);
		
	}
	
	// Move v to the root of the auxiliary tree.
	private void splay(Vertex v) {
		
		// Perform splaying until vertex v does not have a parent, meaning it is the root.
		while (v.parent != null) {
			
			// Find the parent p and grandparent g of vertex v to perform zigging and/or zagging.
			Vertex p = v.parent;
			Vertex g = p.parent;
			
			// If the parent p is the root, the zig operation is performed.
			if (g == null) {
				
				// Zig
				// The tree is rotated on the edge between v and p.
				rotate(v);
				
			// If the parent p is not the root and v and p are either both right children or both left children, the zig-zig operation is performed.
			} else if ((v == p.left && p == g.left) || (v == p.right && p == g.right)) {
				
				// Zig-zig
				// The tree is rotated on the edge between p and g.
				rotate(p);
				// Then, the tree is rotated on the edge between v and p.
				rotate(v);
			
			// If the parent p is not the root and v is a right child and p is a left child, or vice versa, the zig-zag operation is performed.
			} else {
				
				// Zig-zag
				// The tree is rotated on the edge between v and p.
				rotate(v);
				// Then, the tree is rotated on the resulting edge between p and g.
				rotate(v);
				
			}
			
		}
		
	}
	
	private void rotate(Vertex v) {

		// Find the parent p and grandparent g of vertex v to perform corresponding rotations.
		Vertex p = v.parent;
		Vertex g = p.parent;
		
		// If vertex v is the left child of its parent p, rotate right.
		if (v == p.left) {
			
			p.left = v.right;
			v.right = p;
			p.parent = v;
			
			if (p.left != null) {
				p.left.parent = p;
			}
			
		// If vertex v is the right child of its parent p, rotate left.
		} else if (v == p.right) {
			
			p.right = v.left;
			v.left = p;
			p.parent = v;
			
			if (p.right != null) {
				p.right.parent = p;
			}
			
		}
		
		// Set the rotated vertex v parent to be its prior parent's parent (the grandparent) g.
		v.parent = g;
		
		// Set vertex v as either the left or right child of its new parent corresponding to the rotation performed, dependent on the prior parent's position.
		if (g != null) {
			if (p == g.left) {
				g.left = v;
			} else {
				g.right = v;
			}
		}

		// Move the path-parent pointer from p to v, as it could now be the new root.
		v.pathParent = p.pathParent;
		p.pathParent = null;
		
	}

}
